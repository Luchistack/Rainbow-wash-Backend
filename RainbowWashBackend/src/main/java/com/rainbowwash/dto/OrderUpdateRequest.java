package com.rainbowwash.dto;

import lombok.Data;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Pattern;
import java.math.BigDecimal;

@Data
public class OrderUpdateRequest {
    @Pattern(regexp = "(?i)^(Received|Washing|Completed|Delivered)$", message = "Invalid order status")
    private String status;
    @DecimalMin(value = "0.01", message = "Total must be greater than zero")
    private BigDecimal total;
    @Pattern(regexp = "(?i)^(Pending|Sent|Received|Confirmed)$", message = "Invalid payment status")
    private String paymentStatus;
    private Boolean archived;
    private Boolean printed;
    @Pattern(regexp = "(?i)^(paystack|flutterwave|bank|walk-in|transfer|pos|card)$", message = "Unsupported payment method")
    private String paymentMethod;
}