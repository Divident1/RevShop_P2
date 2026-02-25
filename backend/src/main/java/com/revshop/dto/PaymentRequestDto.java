package com.revshop.dto;

import lombok.Data;

@Data
public class PaymentRequestDto {

    private String orderId;

    private String paymentMethod;

}