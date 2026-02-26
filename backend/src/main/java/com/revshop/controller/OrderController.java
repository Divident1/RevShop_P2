package com.revshop.controller;

import com.revshop.dto.*;
import jakarta.validation.Valid;
import com.revshop.service.OrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/orders")
@RequiredArgsConstructor
public class OrderController {

    private final OrderService orderService;



    // Checkout

    @PostMapping("/checkout")
    public OrderResponseDto checkout(
            @Valid @RequestBody CheckoutRequestDto request){

        return orderService.checkout(request);

    }



    // Payment

    @PostMapping("/payment")
    public OrderResponseDto payment(
            @RequestBody PaymentRequestDto request){

        return orderService.processPayment(request);

    }

}
