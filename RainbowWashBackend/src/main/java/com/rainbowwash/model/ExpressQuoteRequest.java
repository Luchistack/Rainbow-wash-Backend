package com.rainbowwash.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@Entity
@Table(name = "express_quote_requests")
public class ExpressQuoteRequest {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String referenceId;

    @Column(nullable = false, length = 120)
    private String fullName;

    @Column(nullable = false, length = 25)
    private String phone;

    @Column(length = 254)
    private String email;

    @Column(nullable = false, length = 120)
    private String service;

    @Column(nullable = false, length = 30)
    private String turnaround;

    @Column(nullable = false, length = 2000)
    private String description;

    @Column(nullable = false, length = 20)
    private String status = "Pending Quote";

    private java.math.BigDecimal quotedPrice;

    @Column(nullable = false)
    private LocalDateTime placedAt = LocalDateTime.now();

    @Column(nullable = false)
    private boolean archived = false;

    @Column(nullable = false)
    private boolean printed = false;
}
