package com.revshop.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class ThresholdRequest {

    @NotNull
    private Integer threshold;

    public Integer getThreshold() {
        return threshold;
    }

    public void setThreshold(Integer threshold) {
        this.threshold = threshold;
    }

}
