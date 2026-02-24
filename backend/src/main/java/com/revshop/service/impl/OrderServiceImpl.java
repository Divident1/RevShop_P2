package com.revshop.service.impl;

import com.revshop.dto.*;
import com.revshop.exception.OrderNotFoundException;
import com.revshop.model.Order;
import com.revshop.repository.OrderRepository;
import com.revshop.service.OrderService;
import com.revshop.util.OrderUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Random;

@Service
@RequiredArgsConstructor
public class OrderServiceImpl implements OrderService {

    private final OrderRepository orderRepository;


    // Checkout

    @Override
    public OrderResponseDto checkout(CheckoutRequestDto request) {

        String orderId = OrderUtil.generateOrderId();

        Order order = Order.builder()
                .orderId(orderId)
                .userId(request.getUserId())
                .shippingAddress(request.getShippingAddress())
                .billingAddress(request.getBillingAddress())
                .totalAmount(request.getTotalAmount())
                .orderStatus("CREATED")
                .paymentStatus("PENDING")
                .createdAt(LocalDateTime.now())
                .build();

        orderRepository.save(order);

        return OrderResponseDto.builder()
                .orderId(orderId)
                .message("Checkout successful")
                .paymentStatus("PENDING")
                .build();

    }



    // Payment

    @Override
    public OrderResponseDto processPayment(PaymentRequestDto request) {

        Order order = orderRepository.findById(request.getOrderId())
                .orElseThrow(() ->
                        new OrderNotFoundException("Order not found"));

        boolean paymentSuccess = simulatePayment();

        if(paymentSuccess){

            order.setPaymentMethod(request.getPaymentMethod());
            order.setPaymentStatus("SUCCESS");
            order.setOrderStatus("CONFIRMED");

            orderRepository.save(order);

            return OrderResponseDto.builder()
                    .orderId(order.getOrderId())
                    .message("Payment Successful. Order Confirmed")
                    .paymentStatus("SUCCESS")
                    .build();

        }
        else{

            order.setPaymentStatus("FAILED");

            orderRepository.save(order);

            return OrderResponseDto.builder()
                    .orderId(order.getOrderId())
                    .message("Payment Failed")
                    .paymentStatus("FAILED")
                    .build();

        }

    }



    // simulate success / failure

    private boolean simulatePayment(){

        Random random = new Random();

        return random.nextBoolean();

    }

}