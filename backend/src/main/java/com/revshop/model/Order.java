package com.revshop.model;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "orders")
public class Order {

    @Id
    private String orderId;

    private String userId;

    private String shippingAddress;

    private String billingAddress;

    private Double totalAmount;

    private String paymentMethod;

    private String paymentStatus;

    private String orderStatus;

    private LocalDateTime createdAt;
}