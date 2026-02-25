package com.revshop.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ProductRequest {

    @NotBlank
    private String name;

    @NotBlank
    private String description;

    @NotNull
    @Positive
    private Double price;

    @NotNull
    @Positive
    private Double mrp;

    @NotBlank
    private String category;

    @NotNull
    @PositiveOrZero
    private Integer quantity;

}