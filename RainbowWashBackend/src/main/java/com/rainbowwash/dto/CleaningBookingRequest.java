package com.rainbowwash.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import java.math.BigDecimal;

@Data
public class CleaningBookingRequest {
    @NotBlank(message = "Service is required")
    private String service;

    @NotBlank(message = "Size is required")
    private String size;

    private String date;
    private String time;
    private String address;
    private String payType;

    @NotNull(message = "Payable amount is required")
    private BigDecimal payable;

    private String transferNote;

    @NotBlank(message = "Full name is required")
    private String fullName;

    @NotBlank(message = "Phone number is required")
    private String phone;

    private String email;
}