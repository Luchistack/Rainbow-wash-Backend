package com.rainbowwash.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

@Data
public class OrderUpdateRequest {

    @Valid
    private List<OrderItemRequest> items;

    @Pattern(regexp = "(?i)^(pickup|dropoff)$", message = "Fulfilment must be pickup or dropoff")
    private String fulfilment;

    @Size(max = 500, message = "Address must be 500 characters or fewer")
    private String address;

    @Size(max = 40, message = "Preferred date is too long")
    private String preferredDate;

    @Size(max = 40, message = "Preferred time is too long")
    private String preferredTime;

    @Pattern(regexp = "(?i)^(paystack|flutterwave|bank|walk-in|transfer|pos|card)$", message = "Unsupported payment method")
    private String paymentMethod;

    @Size(max = 500, message = "Transfer note must be 500 characters or fewer")
    private String transferNote;

    @DecimalMin(value = "0.01", message = "Total must be greater than zero")
    private BigDecimal total;

    @DecimalMin(value = "0.00", message = "Delivery fee cannot be negative")
    private BigDecimal deliveryFee;

    @DecimalMin(value = "0.00", message = "Discount percentage cannot be negative")
    @DecimalMax(value = "100.00", message = "Discount percentage cannot exceed 100")
    private BigDecimal discountPercentage;

    @Pattern(regexp = "(?i)^(Received|Washing|Completed|Delivered)$", message = "Invalid order status")
    private String status;

    @Size(max = 120, message = "Full name must be 120 characters or fewer")
    private String fullName;

    @Pattern(regexp = "^[+0-9() .-]{7,25}$", message = "Phone number contains invalid characters or has an invalid length")
    private String phone;

    @Size(max = 254, message = "Email must be 254 characters or fewer")
    @Pattern(
            regexp = "(?i)^$|^[A-Z0-9.!#$%&'*+/=?^_`{|}~-]+@[A-Z0-9](?:[A-Z0-9-]*[A-Z0-9])?(?:\\.[A-Z0-9](?:[A-Z0-9-]*[A-Z0-9])?)*\\.com$",
            message = "Email must be valid and end in .com"
    )
    private String email;

    @Size(max = 1000, message = "Description must be 1000 characters or fewer")
    private String description;

    @Pattern(regexp = "(?i)^(Pending|Sent|Received|Confirmed)$", message = "Invalid payment status")
    private String paymentStatus;

    private Boolean archived;
    private Boolean printed;
}
