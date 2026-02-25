package com.revshop.service;

import com.revshop.dto.CheckoutRequestDto;
import com.revshop.dto.OrderResponseDto;
import com.revshop.dto.PaymentRequestDto;

public interface OrderService {

    OrderResponseDto checkout(CheckoutRequestDto request);

    OrderResponseDto processPayment(PaymentRequestDto request);

}