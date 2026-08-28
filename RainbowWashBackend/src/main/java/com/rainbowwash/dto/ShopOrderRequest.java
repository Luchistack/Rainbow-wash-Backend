package com.rainbowwash.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

@Data
public class ShopOrderRequest {
    @NotEmpty(message = "Order must contain at least one item")
    @Valid
    private List<ShopOrderItemRequest> items;

    @NotBlank(message = "Full name is required")
    private String fullName;

    @NotBlank(message = "Phone number is required")
    private String phone;

    private String mode;

    @NotNull(message = "Total is required")
    private BigDecimal total;

    private String createdBy;
}
