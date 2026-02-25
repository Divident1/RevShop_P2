package com.revshop.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class OrderResponseDto {

    private String orderId;

    private String message;

    private String paymentStatus;

}