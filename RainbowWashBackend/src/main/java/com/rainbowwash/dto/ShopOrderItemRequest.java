package com.rainbowwash.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class ShopOrderItemRequest {
    @NotBlank(message = "Product name is required")
    @Size(max = 160, message = "Product name must be 160 characters or fewer")
    private String name;

    @Min(value = 1, message = "Quantity must be at least 1")
    private int qty;

    @NotNull(message = "Price is required")
    @DecimalMin(value = "0.00", message = "Price cannot be negative")
    private BigDecimal price;
}
