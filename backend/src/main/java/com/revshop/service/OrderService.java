package com.revshop.service;

import com.revshop.dto.*;
import com.revshop.model.OrderStatus;
import java.util.List;

public interface OrderService {

    // Gotam's Methods (Order Management)
    OrderResponse placeOrder(OrderRequest request);

    List<OrderResponse> getOrdersByBuyer(Long buyerId);

    List<OrderResponse> getOrdersBySeller(Long sellerId);

    OrderResponse getOrderById(Long orderId);

    OrderResponse updateOrderStatus(Long orderId, OrderStatus status);

    void cancelOrder(Long orderId);

    // Anusha's Methods (Checkout & Payment)
    OrderResponseDto checkout(CheckoutRequestDto request);

    OrderResponseDto processPayment(PaymentRequestDto request);
}
