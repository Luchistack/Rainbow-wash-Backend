package com.rainbowwash.dto;

import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class CleaningBookingUpdateRequest {

    @Size(max = 120, message = "Full name must be 120 characters or fewer")
    private String fullName;

    @Pattern(regexp = "^[+0-9() .-]{7,25}$", message = "Phone number contains invalid characters or has an invalid length")
    private String phone;

    @Size(max = 254, message = "Email must be 254 characters or fewer")
    @Pattern(regexp = "(?i)^(?:$|[^@\\s]+@[^@\\s]+[.]com)$", message = "Email must be valid and end in .com")
    private String email;

    @Size(max = 1000, message = "Description must be 1000 characters or fewer")
    private String description;

    @Size(max = 200, message = "Service is too long")
    private String service;

    @Size(max = 200, message = "Size is too long")
    private String size;

    @Size(max = 40, message = "Date is too long")
    private String date;

    @Size(max = 40, message = "Time is too long")
    private String time;

    @Size(max = 500, message = "Address must be 500 characters or fewer")
    private String address;

    @Pattern(regexp = "(?i)^(deposit|full)$", message = "Payment split must be deposit or full")
    private String payType;

    @DecimalMin(value = "0.01", message = "Payable amount must be greater than zero")
    private BigDecimal payable;

    @DecimalMin(value = "0.00", message = "Discount percentage cannot be negative")
    @DecimalMax(value = "100.00", message = "Discount percentage cannot exceed 100")
    private BigDecimal discountPercentage;

    @Size(max = 500, message = "Transfer note must be 500 characters or fewer")
    private String transferNote;

    @Pattern(regexp = "(?i)^(Pending|Sent|Received|Confirmed)$", message = "Invalid payment status")
    private String paymentStatus;

    @Pattern(regexp = "(?i)^(Transfer|Card|POS)$", message = "Unsupported payment method")
    private String paymentMethod;

    @Pattern(regexp = "(?i)^(Pending Quote|Confirmed|In Progress|Completed|Cancelled)$", message = "Invalid booking status")
    private String status;

    private Boolean archived;
    private Boolean printed;
}
