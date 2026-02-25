package com.revshop.dto;

import lombok.Data;

@Data
public class CheckoutRequestDto {

    private String userId;

    private String shippingAddress;

    private String billingAddress;

    private Double totalAmount;

}