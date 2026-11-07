package com.rainbowwash.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SubscriptionPurchaseResponse {

    private Long id;
    private String referenceId;
    private String customerName;
    private String customerPhone;
    private Long planId;
    private String planName;
    private String planDuration;
    private String planCovers;
    private BigDecimal kgAllowance;
    private BigDecimal subtotal;
    private BigDecimal vatRate;
    private BigDecimal vatAmount;
    private BigDecimal total;
    private LocalDate startDate;
    private LocalDate expiryDate;
    private String createdByName;
    private String createdByEmail;
    private String paymentMethod;
    private String paymentStatus;
    private LocalDateTime placedAt;
    private boolean printed;
}
