package com.rainbowwash.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.time.LocalDate;

@Data
public class SubscriptionPurchaseRequest {

    @NotBlank(message = "Customer name is required")
    @Size(max = 150, message = "Customer name must be 150 characters or fewer")
    private String customerName;

    @NotBlank(message = "Customer phone is required")
    @Size(max = 40, message = "Customer phone must be 40 characters or fewer")
    private String customerPhone;

    @NotNull(message = "Subscription plan is required")
    private Long planId;

    @NotNull(message = "Subscription start date is required")
    private LocalDate startDate;

    @NotNull(message = "Subscription expiry date is required")
    private LocalDate expiryDate;

    @NotBlank(message = "Payment method is required")
    @Pattern(regexp = "(?i)^(Transfer|POS)$", message = "Payment method must be Transfer or POS")
    private String paymentMethod;
}
