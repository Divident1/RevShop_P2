package com.revshop.service.impl;

import com.revshop.dto.*;
import com.revshop.exception.InsufficientStockException;
import com.revshop.exception.OrderNotFoundException;
import com.revshop.exception.ResourceNotFoundException;
import com.revshop.model.*;
import com.revshop.repository.OrderRepository;
import com.revshop.repository.ProductRepository;
import com.revshop.repository.UserRepository;
import com.revshop.service.OrderService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.revshop.service.NotificationService;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class OrderServiceImpl implements OrderService {

    private final OrderRepository orderRepository;
    private final UserRepository userRepository;
    private final ProductRepository productRepository;
    private final NotificationService notificationService;

    public OrderServiceImpl(OrderRepository orderRepository,
            UserRepository userRepository,
            ProductRepository productRepository,
            NotificationService notificationService) {
        this.orderRepository = orderRepository;
        this.userRepository = userRepository;
        this.productRepository = productRepository;
        this.notificationService = notificationService;
    }

    // ══════════════════════════════════════════════════════════════════
    // Place Order
    // ══════════════════════════════════════════════════════════════════

    @Override
    @Transactional
    public OrderResponse placeOrder(OrderRequest request) {
        User buyer = findUserById(request.getBuyerId());

        Order order = new Order();
        order.setBuyer(buyer);
        order.setShippingAddress(request.getShippingAddress());
        order.setPaymentMethod(request.getPaymentMethod());
        order.setStatus(OrderStatus.PENDING);
        order.setOrderDate(LocalDateTime.now());

        double totalAmount = 0;

        for (OrderRequest.OrderItemRequest itemRequest : request.getItems()) {
            Product product = findProductById(itemRequest.getProductId());
            validateStock(product, itemRequest.getQuantity());

            OrderItem orderItem = new OrderItem();
            orderItem.setProduct(product);
            orderItem.setQuantity(itemRequest.getQuantity());
            orderItem.setPriceAtPurchase(product.getPrice());

            order.addOrderItem(orderItem);
            totalAmount += orderItem.getSubtotal();

            product.setQuantity(product.getQuantity() - itemRequest.getQuantity());
            productRepository.save(product);

            // Notify Seller
            notificationService.createNotification(
                    product.getSeller().getId(),
                    "🎉 New Order Received! Someone just bought " + itemReq.getQuantity() + "x of your '"
                            + product.getName() + "'.");

            // Low Stock Alert
            if (product.getQuantity() <= product.getStockThreshold()) {
                notificationService.createNotification(
                        product.getSeller().getId(),
                        "🚨 LOW STOCK ALERT: " + product.getName() + " only has " + product.getQuantity() + " left!");
            }
        }

        order.setTotalAmount(totalAmount);
        Order savedOrder = orderRepository.save(order);

        // Notify Buyer
        notificationService.createNotification(
                buyer.getId(),
                "✅ Thank you for your purchase! Your Order #" + savedOrder.getId()
                        + " has been placed successfully and given to the seller.");

        return mapToResponse(savedOrder);
    }

    // ══════════════════════════════════════════════════════════════════
    // Get Orders
    // ══════════════════════════════════════════════════════════════════

    @Override
    public List<OrderResponse> getOrdersByBuyer(Long buyerId) {
        return orderRepository.findByBuyerIdOrderByOrderDateDesc(buyerId)
                .stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Override
    public List<OrderResponse> getOrdersBySeller(Long sellerId) {
        return orderRepository.findByOrderItems_Product_Seller_IdOrderByOrderDateDesc(sellerId)
                .stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Override
    public OrderResponse getOrderById(Long orderId) {
        Order order = findOrderById(orderId);
        return mapToResponse(order);
    }

    // ══════════════════════════════════════════════════════════════════
    // Update & Cancel
    // ══════════════════════════════════════════════════════════════════

    @Override
    @Transactional
    public OrderResponse updateOrderStatus(Long orderId, OrderStatus status) {
        Order order = findOrderById(orderId);
        order.setStatus(status);
        order.setUpdatedAt(LocalDateTime.now());

        Order updatedOrder = orderRepository.save(order);

        // Notify Buyer about Status change
        String customMessage = "Update on Order #" + order.getId() + ": Status changed to " + status.name();
        if (status == OrderStatus.SHIPPED) {
            customMessage = "📦 Good News! Your Order #" + order.getId() + " has been SHIPPED.";
        } else if (status == OrderStatus.DELIVERED) {
            customMessage = "🏡 Delivered! Your Order #" + order.getId() + " has arrived safely. Thanks for shopping!";
        } else if (status == OrderStatus.CANCELLED) {
            customMessage = "❌ Order #" + order.getId() + " has been CANCELLED.";
        }

        notificationService.createNotification(order.getBuyer().getId(), customMessage);

        return mapToResponse(updatedOrder);
    }

    @Override
    @Transactional
    public void cancelOrder(Long orderId) {
        Order order = findOrderById(orderId);

        if (order.getStatus() == OrderStatus.SHIPPED || order.getStatus() == OrderStatus.DELIVERED) {
            throw new IllegalStateException("Cannot cancel order that is already " + order.getStatus());
        }

        // Restore stock for each cancelled item
        for (OrderItem item : order.getOrderItems()) {
            Product product = item.getProduct();
            product.setQuantity(product.getQuantity() + item.getQuantity());
            productRepository.save(product);
        }

        order.setStatus(OrderStatus.CANCELLED);
        order.setUpdatedAt(LocalDateTime.now());
        orderRepository.save(order);
    }

    // ══════════════════════════════════════════════════════════════════
    // Checkout & Payment
    // ══════════════════════════════════════════════════════════════════

    @Override
    public OrderResponseDto checkout(CheckoutRequestDto request) {
        User buyer = findUserById(Long.parseLong(request.getUserId()));

        Order order = new Order();
        order.setBuyer(buyer);
        order.setName(request.getName());
        order.setPhoneNumber(request.getPhoneNumber());
        order.setShippingAddress(request.getShippingAddress());
        order.setBillingAddress(request.getBillingAddress());
        order.setTotalAmount(request.getTotalAmount());
        order.setStatus(OrderStatus.PENDING);
        order.setPaymentStatus("PENDING");
        order.setOrderDate(LocalDateTime.now());

        Order savedOrder = orderRepository.save(order);

        return OrderResponseDto.builder()
                .orderId(String.valueOf(savedOrder.getId()))
                .message("Checkout successful")
                .paymentStatus("PENDING")
                .build();
    }

    @Override
    public OrderResponseDto processPayment(PaymentRequestDto request) {
        Order order = findOrderById(Long.parseLong(request.getOrderId()));

        String paymentMethod = normalizePaymentMethod(request.getPaymentMethod());
        boolean paymentSuccess = isValidPaymentMethod(paymentMethod);

        if (paymentSuccess) {
            order.setPaymentMethod(paymentMethod);
            order.setPaymentStatus("SUCCESS");
            order.setStatus(OrderStatus.CONFIRMED);
            orderRepository.save(order);

            return OrderResponseDto.builder()
                    .orderId(String.valueOf(order.getId()))
                    .message("Payment Successful. Order Confirmed")
                    .paymentStatus("SUCCESS")
                    .build();
        } else {
            order.setPaymentStatus("FAILED");
            orderRepository.save(order);

            return OrderResponseDto.builder()
                    .orderId(String.valueOf(order.getId()))
                    .message("Payment Failed")
                    .paymentStatus("FAILED")
                    .build();
        }
    }

    // ══════════════════════════════════════════════════════════════════
    // DRY Helpers — reusable lookup & validation methods
    // ══════════════════════════════════════════════════════════════════

    private User findUserById(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + userId));
    }

    private Product findProductById(Long productId) {
        return productRepository.findById(productId)
                .orElseThrow(() -> new ResourceNotFoundException("Product not found with id: " + productId));
    }

    private Order findOrderById(Long orderId) {
        return orderRepository.findById(orderId)
                .orElseThrow(() -> new OrderNotFoundException("Order not found with id: " + orderId));
    }

    private void validateStock(Product product, int requestedQuantity) {
        if (product.getQuantity() < requestedQuantity) {
            throw new InsufficientStockException(
                    "Insufficient stock for '" + product.getName() +
                            "': requested=" + requestedQuantity +
                            ", available=" + product.getQuantity());
        }
    }

    private String normalizePaymentMethod(String paymentMethod) {
        return paymentMethod == null ? "" : paymentMethod.trim().toUpperCase();
    }

    private boolean isValidPaymentMethod(String paymentMethod) {
        return "COD".equals(paymentMethod)
                || "CREDIT_CARD".equals(paymentMethod)
                || "DEBIT_CARD".equals(paymentMethod);
    }

    private OrderResponse mapToResponse(Order order) {
        OrderResponse response = new OrderResponse();
        response.setOrderId(order.getId());
        response.setBuyerName(order.getBuyer().getName());
        response.setBuyerEmail(order.getBuyer().getEmail());
        response.setStatus(order.getStatus());
        response.setTotalAmount(order.getTotalAmount());
        response.setShippingAddress(order.getShippingAddress());
        response.setPaymentMethod(order.getPaymentMethod());
        response.setOrderDate(order.getOrderDate());

        List<OrderResponse.OrderItemResponse> items = order.getOrderItems().stream()
                .map(item -> {
                    OrderResponse.OrderItemResponse itemResponse = new OrderResponse.OrderItemResponse();
                    itemResponse.setProductId(item.getProduct().getId());
                    itemResponse.setProductName(item.getProduct().getName());
                    itemResponse.setQuantity(item.getQuantity());
                    itemResponse.setPriceAtPurchase(item.getPriceAtPurchase());
                    itemResponse.setSubtotal(item.getSubtotal());
                    return itemResponse;
                })
                .collect(Collectors.toList());

        response.setItems(items);
        return response;
    }
}
