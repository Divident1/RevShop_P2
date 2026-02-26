package com.revshop.service.impl;

import com.revshop.dto.*;
import com.revshop.model.*;
import com.revshop.repository.OrderRepository;
import com.revshop.repository.ProductRepository;
import com.revshop.repository.UserRepository;
import com.revshop.service.OrderService;
import com.revshop.util.OrderUtil;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;

@Service
public class OrderServiceImpl implements OrderService {

    private final OrderRepository orderRepository;
    private final UserRepository userRepository;
    private final ProductRepository productRepository;

    public OrderServiceImpl(OrderRepository orderRepository,
            UserRepository userRepository,
            ProductRepository productRepository) {
        this.orderRepository = orderRepository;
        this.userRepository = userRepository;
        this.productRepository = productRepository;
    }

    // ══════════════════════════════════════════════════════════════════════
    // Gotam's Order Management Methods
    // ══════════════════════════════════════════════════════════════════════

    @Override
    @Transactional
    public OrderResponse placeOrder(OrderRequest request) {

        User buyer = userRepository.findById(request.getBuyerId())
                .orElseThrow(() -> new RuntimeException("Buyer not found"));

        Order order = new Order();
        order.setBuyer(buyer);
        order.setShippingAddress(request.getShippingAddress());
        order.setPaymentMethod(request.getPaymentMethod());
        order.setStatus(OrderStatus.PENDING);
        order.setOrderDate(LocalDateTime.now());

        double totalAmount = 0;

        for (OrderRequest.OrderItemRequest itemReq : request.getItems()) {

            Product product = productRepository.findById(itemReq.getProductId())
                    .orElseThrow(() -> new RuntimeException("Product not found: " + itemReq.getProductId()));

            if (product.getQuantity() < itemReq.getQuantity()) {
                throw new RuntimeException("Insufficient stock for product: " + product.getName());
            }

            OrderItem orderItem = new OrderItem();
            orderItem.setProduct(product);
            orderItem.setQuantity(itemReq.getQuantity());
            orderItem.setPriceAtPurchase(product.getPrice());

            order.addOrderItem(orderItem);

            totalAmount += orderItem.getSubtotal();

            // Reduce stock
            product.setQuantity(product.getQuantity() - itemReq.getQuantity());
            productRepository.save(product);
        }

        order.setTotalAmount(totalAmount);
        Order savedOrder = orderRepository.save(order);

        return mapToResponse(savedOrder);
    }

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
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new RuntimeException("Order not found"));
        return mapToResponse(order);
    }

    @Override
    @Transactional
    public OrderResponse updateOrderStatus(Long orderId, OrderStatus status) {

        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new RuntimeException("Order not found"));

        order.setStatus(status);
        order.setUpdatedAt(LocalDateTime.now());

        Order updatedOrder = orderRepository.save(order);
        return mapToResponse(updatedOrder);
    }

    @Override
    @Transactional
    public void cancelOrder(Long orderId) {

        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new RuntimeException("Order not found"));

        if (order.getStatus() == OrderStatus.SHIPPED || order.getStatus() == OrderStatus.DELIVERED) {
            throw new RuntimeException("Cannot cancel order that is already shipped or delivered");
        }

        // Restore stock
        for (OrderItem item : order.getOrderItems()) {
            Product product = item.getProduct();
            product.setQuantity(product.getQuantity() + item.getQuantity());
            productRepository.save(product);
        }

        order.setStatus(OrderStatus.CANCELLED);
        order.setUpdatedAt(LocalDateTime.now());
        orderRepository.save(order);
    }

    // ══════════════════════════════════════════════════════════════════════
    // Anusha's Checkout & Payment Methods
    // ══════════════════════════════════════════════════════════════════════

    @Override
    public OrderResponseDto checkout(CheckoutRequestDto request) {

        User buyer = userRepository.findById(Long.parseLong(request.getUserId()))
                .orElseThrow(() -> new RuntimeException("User not found"));

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

        Order order = orderRepository.findById(Long.parseLong(request.getOrderId()))
                .orElseThrow(() -> new RuntimeException("Order not found"));

        boolean paymentSuccess = simulatePayment();

        if (paymentSuccess) {
            order.setPaymentMethod(request.getPaymentMethod());
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

    // simulate success / failure
    private boolean simulatePayment() {
        Random random = new Random();
        return random.nextBoolean();
    }

    // ══════════════════════════════════════════════════════════════════════
    // Helper: Map Order entity to OrderResponse DTO
    // ══════════════════════════════════════════════════════════════════════

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
