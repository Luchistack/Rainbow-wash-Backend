package com.rainbowwash.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class ExpressPricingOptionRequest {

    @NotBlank(message = "Turnaround is required")
    @Size(max = 80, message = "Turnaround must be 80 characters or fewer")
    private String turnaround;

    @NotBlank(message = "Pricing unit is required")
    @Size(max = 80, message = "Pricing unit must be 80 characters or fewer")
    private String pricingUnit;

    @NotNull(message = "Price is required")
    @DecimalMin(value = "0.01", message = "Price must be greater than zero")
    private BigDecimal price;

    private boolean available = true;
}
