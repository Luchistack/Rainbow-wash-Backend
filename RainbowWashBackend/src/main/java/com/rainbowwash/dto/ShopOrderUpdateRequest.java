package com.rainbowwash.dto;

import lombok.Data;
import jakarta.validation.constraints.Pattern;

@Data
public class ShopOrderUpdateRequest {
    @Pattern(regexp = "(?i)^(Received|Packed|Out for Delivery|Completed|Cancelled)$", message = "Invalid shop order status")
    private String status;
    @Pattern(regexp = "(?i)^(Pending|Sent|Received|Confirmed)$", message = "Invalid payment status")
    private String paymentStatus;
    private Boolean archived;
    private Boolean printed;
    @Pattern(regexp = "(?i)^(Transfer|Card|POS)$", message = "Unsupported payment method")
    private String paymentMethod;
}