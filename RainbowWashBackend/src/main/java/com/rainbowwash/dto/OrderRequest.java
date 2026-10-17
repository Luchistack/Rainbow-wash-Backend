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
public class OrderRequest {
    @NotEmpty(message = "Order must contain at least one item")
    @Valid
    private List<OrderItemRequest> items;

    @NotBlank(message = "Fulfilment is required")
    @Pattern(regexp = "(?i)^(pickup|dropoff)$", message = "Fulfilment must be pickup or dropoff")
    private String fulfilment;
    @Size(max = 500, message = "Address must be 500 characters or fewer")
    private String address;
    @Size(max = 40, message = "Preferred date is too long")
    private String preferredDate;
    @Size(max = 40, message = "Preferred time is too long")
    private String preferredTime;
    @NotBlank(message = "Payment method is required")
    @Pattern(regexp = "(?i)^(paystack|flutterwave|bank|walk-in|transfer|pos|card)$", message = "Unsupported payment method")
    private String paymentMethod;
    @Size(max = 500, message = "Transfer note must be 500 characters or fewer")
    private String transferNote;

    @NotNull(message = "Total is required")
    @DecimalMin(value = "0.01", message = "Total must be greater than zero")
    private BigDecimal total;

    @DecimalMin(value = "0.00", message = "Delivery fee cannot be negative")
    private BigDecimal deliveryFee;

    @NotBlank(message = "Full name is required")
    @Size(max = 120, message = "Full name must be 120 characters or fewer")
    private String fullName;

    @NotBlank(message = "Phone number is required")
    @Pattern(regexp = "^[+0-9() .-]{7,25}$", message = "Phone number contains invalid characters or has an invalid length")
    private String phone;

    @Size(max = 254, message = "Email must be 254 characters or fewer")
    @Pattern(regexp = "(?i)^$|^[A-Z0-9.!#$%&'*+/=?^_`{|}~-]+@[A-Z0-9](?:[A-Z0-9-]*[A-Z0-9])?(?:\\.[A-Z0-9](?:[A-Z0-9-]*[A-Z0-9])?)*\\.com$", message = "Email must be valid and end in .com")
    private String email;

    // Set by the dashboard when staff create a walk-in order; left null for
    // orders customers place themselves on the public site.
    private String createdBy;
}
