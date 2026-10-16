package com.rainbowwash.dto;

import lombok.Data;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Pattern;
import java.math.BigDecimal;

@Data
public class CleaningBookingUpdateRequest {
    @Pattern(regexp = "(?i)^(Pending Quote|Confirmed|In Progress|Completed|Cancelled)$", message = "Invalid booking status")
    private String status;
    @DecimalMin(value = "0.01", message = "Payable amount must be greater than zero")
    private BigDecimal payable;
    @Pattern(regexp = "(?i)^(Pending|Sent|Received|Confirmed)$", message = "Invalid payment status")
    private String paymentStatus;
    private Boolean archived;
    private Boolean printed;
    @Pattern(regexp = "(?i)^(Transfer|Card|POS)$", message = "Unsupported payment method")
    private String paymentMethod;
}