package com.rainbowwash.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Data;

@Data
public class SubscriptionPurchasePaymentUpdateRequest {

    @NotBlank(message = "Payment status is required")
    @Pattern(
        regexp = "(?i)^(Pending|Sent|Received|Confirmed)$",
        message = "Invalid payment status"
    )
    private String paymentStatus;
}
