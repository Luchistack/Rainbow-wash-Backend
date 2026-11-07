package com.rainbowwash.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "subscription_purchases")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class SubscriptionPurchase {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String referenceId;

    @Column(nullable = false)
    private String customerName;

    @Column(nullable = false)
    private String customerPhone;

    @Column(nullable = false)
    private Long planId;

    @Column(nullable = false)
    private String planName;

    @Column(nullable = false)
    private String planDuration;

    @Column(nullable = false, length = 1000)
    private String planCovers;

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal kgAllowance;

    @Column(nullable = false, precision = 12, scale = 2)
    private BigDecimal subtotal;

    @Column(nullable = false, precision = 5, scale = 4)
    private BigDecimal vatRate;

    @Column(nullable = false, precision = 12, scale = 2)
    private BigDecimal vatAmount;

    @Column(nullable = false, precision = 12, scale = 2)
    private BigDecimal total;

    @Column(nullable = false)
    private LocalDate startDate;

    @Column(nullable = false)
    private LocalDate expiryDate;

    @Column(nullable = false)
    private String createdByName;

    @Column(nullable = false)
    private String createdByEmail;

    @Column(nullable = false)
    private String paymentMethod;

    @Column(nullable = false)
    private String paymentStatus = "Pending";

    @Column(nullable = false)
    private LocalDateTime placedAt;

    @Column(nullable = false)
    private boolean printed = false;

    @PrePersist
    protected void onCreate() {
        if (placedAt == null) {
            placedAt = LocalDateTime.now();
        }
        if (paymentStatus == null) {
            paymentStatus = "Pending";
        }
    }
}
