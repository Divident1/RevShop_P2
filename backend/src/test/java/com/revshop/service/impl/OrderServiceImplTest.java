package com.revshop.service.impl;

import com.revshop.dto.*;
import com.revshop.exception.InsufficientStockException;
import com.revshop.exception.OrderNotFoundException;
import com.revshop.model.*;
import com.revshop.repository.OrderRepository;
import com.revshop.repository.ProductRepository;
import com.revshop.repository.UserRepository;
import com.revshop.service.NotificationService;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class OrderServiceImplTest {

    @Mock
    private OrderRepository orderRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private ProductRepository productRepository;

    @Mock
    private NotificationService notificationService;

    @InjectMocks
    private OrderServiceImpl orderService;

    private User buyer;
    private Product product;
    private Order order;

    @BeforeEach
    void setUp() {
        buyer = new User();
        buyer.setId(1L);
        buyer.setName("TestBuyer");
        buyer.setEmail("buyer@revshop.com");
        buyer.setRole(Role.BUYER);

        User seller = new User();
        seller.setId(2L);
        seller.setName("TestSeller");
        seller.setRole(Role.SELLER);

        product = new Product();
        product.setId(1L);
        product.setName("Test Phone");
        product.setPrice(15000.0);
        product.setMrp(20000.0);
        product.setQuantity(50);
        product.setSeller(seller);

        order = new Order();
        order.setId(1L);
        order.setBuyer(buyer);
        order.setStatus(OrderStatus.PENDING);
        order.setTotalAmount(15000.0);
        order.setShippingAddress("123 Test St");
        order.setPaymentMethod("COD");
        order.setOrderDate(LocalDateTime.now());
        order.setOrderItems(new ArrayList<>());
    }

    @Test
    @DisplayName("PlaceOrder - should place order successfully")
    void placeOrder_Success() {
        OrderRequest request = new OrderRequest();
        request.setBuyerId(1L);
        request.setShippingAddress("123 Test St");
        request.setPaymentMethod("COD");

        OrderRequest.OrderItemRequest itemReq = new OrderRequest.OrderItemRequest();
        itemReq.setProductId(1L);
        itemReq.setQuantity(2);
        request.setItems(List.of(itemReq));

        when(userRepository.findById(1L)).thenReturn(Optional.of(buyer));
        when(productRepository.findById(1L)).thenReturn(Optional.of(product));
        when(productRepository.save(any(Product.class))).thenReturn(product);
        when(orderRepository.save(any(Order.class))).thenAnswer(inv -> {
            Order savedOrder = inv.getArgument(0);
            savedOrder.setId(1L);
            return savedOrder;
        });

        OrderResponse result = orderService.placeOrder(request);

        assertNotNull(result);
        verify(orderRepository, times(1)).save(any(Order.class));
    }

    @Test
    @DisplayName("PlaceOrder - should throw when insufficient stock")
    void placeOrder_InsufficientStock() {
        product.setQuantity(1); // only 1 in stock

        OrderRequest request = new OrderRequest();
        request.setBuyerId(1L);

        OrderRequest.OrderItemRequest itemReq = new OrderRequest.OrderItemRequest();
        itemReq.setProductId(1L);
        itemReq.setQuantity(5); // trying to order 5
        request.setItems(List.of(itemReq));

        when(userRepository.findById(1L)).thenReturn(Optional.of(buyer));
        when(productRepository.findById(1L)).thenReturn(Optional.of(product));

        assertThrows(InsufficientStockException.class, () -> orderService.placeOrder(request));
    }

    @Test
    @DisplayName("GetOrdersByBuyer - should return buyer's orders")
    void getOrdersByBuyer_Success() {
        when(orderRepository.findByBuyerIdOrderByOrderDateDesc(1L))
                .thenReturn(List.of(order));

        List<OrderResponse> results = orderService.getOrdersByBuyer(1L);

        assertEquals(1, results.size());
        assertEquals("TestBuyer", results.get(0).getBuyerName());
    }

    @Test
    @DisplayName("GetOrderById - should return the order")
    void getOrderById_Success() {
        when(orderRepository.findById(1L)).thenReturn(Optional.of(order));

        OrderResponse result = orderService.getOrderById(1L);

        assertNotNull(result);
        assertEquals(1L, result.getOrderId());
    }

    @Test
    @DisplayName("GetOrderById - should throw when order not found")
    void getOrderById_NotFound() {
        when(orderRepository.findById(999L)).thenReturn(Optional.empty());

        assertThrows(OrderNotFoundException.class, () -> orderService.getOrderById(999L));
    }

    @Test
    @DisplayName("UpdateOrderStatus - should update status to SHIPPED")
    void updateOrderStatus_Success() {
        when(orderRepository.findById(1L)).thenReturn(Optional.of(order));
        when(orderRepository.save(any(Order.class))).thenReturn(order);

        OrderResponse result = orderService.updateOrderStatus(1L, OrderStatus.SHIPPED);

        assertNotNull(result);
        verify(orderRepository, times(1)).save(any(Order.class));
    }

    @Test
    @DisplayName("CancelOrder - should cancel PENDING order and restore stock")
    void cancelOrder_Success() {
        OrderItem orderItem = new OrderItem();
        orderItem.setProduct(product);
        orderItem.setQuantity(2);
        order.getOrderItems().add(orderItem);

        when(orderRepository.findById(1L)).thenReturn(Optional.of(order));
        when(productRepository.save(any(Product.class))).thenReturn(product);
        when(orderRepository.save(any(Order.class))).thenReturn(order);

        orderService.cancelOrder(1L);

        assertEquals(OrderStatus.CANCELLED, order.getStatus());
        assertEquals(52, product.getQuantity()); // 50 + 2 restored
    }

    @Test
    @DisplayName("CancelOrder - should throw when order is SHIPPED")
    void cancelOrder_AlreadyShipped() {
        order.setStatus(OrderStatus.SHIPPED);

        when(orderRepository.findById(1L)).thenReturn(Optional.of(order));

        Exception exception = assertThrows(IllegalStateException.class,
                () -> orderService.cancelOrder(1L));

        assertTrue(exception.getMessage().contains("SHIPPED"));
    }

    @Test
    @DisplayName("CancelOrder - should throw when order is DELIVERED")
    void cancelOrder_AlreadyDelivered() {
        order.setStatus(OrderStatus.DELIVERED);

        when(orderRepository.findById(1L)).thenReturn(Optional.of(order));

        assertThrows(IllegalStateException.class, () -> orderService.cancelOrder(1L));
    }

    @Test
    @DisplayName("Checkout - should create order from checkout")
    void checkout_Success() {
        CheckoutRequestDto request = new CheckoutRequestDto();
        request.setUserId("1");
        request.setName("TestBuyer");
        request.setPhoneNumber("1234567890");
        request.setShippingAddress("123 Test St");
        request.setTotalAmount(15000.0);

        when(userRepository.findById(1L)).thenReturn(Optional.of(buyer));
        when(orderRepository.save(any(Order.class))).thenAnswer(inv -> {
            Order savedOrder = inv.getArgument(0);
            savedOrder.setId(10L);
            return savedOrder;
        });

        OrderResponseDto result = orderService.checkout(request);

        assertNotNull(result);
        assertEquals("10", result.getOrderId());
        assertEquals("Checkout successful", result.getMessage());
        assertEquals("PENDING", result.getPaymentStatus());
    }

    @Test
    @DisplayName("ProcessPayment - should succeed with COD")
    void processPayment_COD_Success() {
        PaymentRequestDto request = new PaymentRequestDto();
        request.setOrderId("1");
        request.setPaymentMethod("COD");

        when(orderRepository.findById(1L)).thenReturn(Optional.of(order));
        when(orderRepository.save(any(Order.class))).thenReturn(order);

        OrderResponseDto result = orderService.processPayment(request);

        assertEquals("SUCCESS", result.getPaymentStatus());
        assertTrue(result.getMessage().contains("Successful"));
    }

    @Test
    @DisplayName("ProcessPayment - should fail with unsupported method")
    void processPayment_UnsupportedMethod() {
        PaymentRequestDto request = new PaymentRequestDto();
        request.setOrderId("1");
        request.setPaymentMethod("BITCOIN");

        when(orderRepository.findById(1L)).thenReturn(Optional.of(order));
        when(orderRepository.save(any(Order.class))).thenReturn(order);

        OrderResponseDto result = orderService.processPayment(request);

        assertEquals("FAILED", result.getPaymentStatus());
        assertTrue(result.getMessage().contains("Failed"));
    }
}
