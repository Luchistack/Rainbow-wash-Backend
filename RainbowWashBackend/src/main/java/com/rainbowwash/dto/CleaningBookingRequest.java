package com.rainbowwash.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;
import java.math.BigDecimal;

@Data
public class CleaningBookingRequest {
    @NotBlank(message = "Service is required")
    private String service;

    @NotBlank(message = "Size is required")
    private String size;

    @NotBlank(message = "Date is required")
    @Size(max = 40, message = "Date is too long")
    private String date;
    @NotBlank(message = "Time is required")
    @Size(max = 40, message = "Time is too long")
    private String time;
    @NotBlank(message = "Address is required")
    @Size(max = 500, message = "Address must be 500 characters or fewer")
    private String address;
    @NotBlank(message = "Payment split is required")
    @Pattern(regexp = "(?i)^(deposit|full)$", message = "Payment split must be deposit or full")
    private String payType;

    @NotNull(message = "Payable amount is required")
    @DecimalMin(value = "0.01", message = "Payable amount must be greater than zero")
    private BigDecimal payable;

    @Size(max = 500, message = "Transfer note must be 500 characters or fewer")
    private String transferNote;

    @NotBlank(message = "Full name is required")
    @Size(max = 120, message = "Full name must be 120 characters or fewer")
    private String fullName;

    @NotBlank(message = "Phone number is required")
    @Pattern(regexp = "^[+0-9() .-]{7,25}$", message = "Phone number contains invalid characters or has an invalid length")
    private String phone;

    @NotBlank(message = "Email is required")
    @Size(max = 254, message = "Email must be 254 characters or fewer")
    @Pattern(regexp = "(?i)^[A-Z0-9.!#$%&'*+/=?^_`{|}~-]+@[A-Z0-9](?:[A-Z0-9-]*[A-Z0-9])?(?:\\.[A-Z0-9](?:[A-Z0-9-]*[A-Z0-9])?)*\\.com$", message = "Email must be valid and end in .com")
    private String email;
}