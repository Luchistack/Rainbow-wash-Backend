package com.rainbowwash.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import java.util.List;

@Data
public class OrderRequest {
    @NotNull(message = "User ID is required")
    private Long userId;

    @NotBlank(message = "Delivery address is required")
    private String deliveryAddress;

    @NotEmpty(message = "Order must contain at least one item")
    private List<OrderItemRequest> items;
}