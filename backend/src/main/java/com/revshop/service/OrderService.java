package com.revshop.service;

import com.revshop.dto.OrderRequest;
import com.revshop.dto.OrderResponse;
import com.revshop.model.OrderStatus;

import java.util.List;

public interface OrderService {

    OrderResponse placeOrder(OrderRequest request);

    List<OrderResponse> getOrdersByBuyer(Long buyerId);

    List<OrderResponse> getOrdersBySeller(Long sellerId);

    OrderResponse getOrderById(Long orderId);

    OrderResponse updateOrderStatus(Long orderId, OrderStatus status);

    void cancelOrder(Long orderId);

}
