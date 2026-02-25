package com.revshop.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
@Table(name = "products")
public class Product {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false, length = 1000)
    private String description;

    @Column(nullable = false)
    private Double price;

    @Column(nullable = false)
    private Double mrp;

    private Double discountPercentage;

    @Column(nullable = false)
    private String category;

    @Column(nullable = false)
    private Integer quantity;

    @Column(nullable = false)
    private Long sellerId;

    private Boolean isActive = true;

    @Column(nullable = false)
    private Integer stockThreshold = 5;   // default value

    // getters & setters

}