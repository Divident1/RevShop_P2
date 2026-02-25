package com.revshop.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class ThresholdRequest {

    @NotNull
    private Integer threshold;

    private Long sellerId;
}
