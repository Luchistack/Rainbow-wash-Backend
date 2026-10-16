package com.rainbowwash.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

@Data
public class ShopOrderRequest {
    @NotEmpty(message = "Order must contain at least one item")
    @Valid
    private List<ShopOrderItemRequest> items;

    @NotBlank(message = "Full name is required")
    @Size(max = 120, message = "Full name must be 120 characters or fewer")
    private String fullName;

    @NotBlank(message = "Phone number is required")
    @Pattern(regexp = "^[+0-9() .-]{7,25}$", message = "Phone number contains invalid characters or has an invalid length")
    private String phone;

    @Pattern(regexp = "(?i)^(|pickup|delivery)$", message = "Mode must be pickup or delivery")
    private String mode;

    @NotNull(message = "Total is required")
    @DecimalMin(value = "0.01", message = "Total must be greater than zero")
    private BigDecimal total;

    private String createdBy;
}
