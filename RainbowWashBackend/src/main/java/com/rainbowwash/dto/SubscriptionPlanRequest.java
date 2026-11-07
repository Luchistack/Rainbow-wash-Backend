package com.rainbowwash.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class SubscriptionPlanRequest {

    @NotBlank(message = "Plan name is required")
    private String name;

    @NotNull(message = "Price is required")
    @DecimalMin(value = "0.0", inclusive = false, message = "Price must be greater than zero")
    private BigDecimal price;

    @NotBlank(message = "Duration is required")
    private String duration;

    @NotBlank(message = "Coverage details are required")
    private String covers;

    @NotNull(message = "KG allowance is required")
    @DecimalMin(value = "0.01", message = "KG allowance must be greater than zero")
    private BigDecimal kgAllowance;

    private boolean active = true;
}
