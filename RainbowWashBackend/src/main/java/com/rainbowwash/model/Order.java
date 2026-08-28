package com.rainbowwash.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Data
@NoArgsConstructor
@Entity
@Table(name = "orders")
public class Order {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String referenceId;

    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<OrderItem> items = new ArrayList<>();

    private String fulfilment; // "pickup" | "dropoff"
    private String address;
    private String preferredDate;
    private String preferredTime;
    private String paymentMethod; // "paystack" | "flutterwave" | "bank" | "walk-in"
    private String transferNote;
    private String paymentStatus; // "Pending" | "Sent" | "Confirmed"

    @Column(nullable = false)
    private BigDecimal total;

    private String status; // "Received" | "Washing" | "Completed" | "Delivered"

    private String fullName;
    private String phone;
    private String email;

    // Name of the staff member who created this as a walk-in order, null for
    // orders customers placed themselves on the public site.
    private String createdBy;

    private LocalDateTime placedAt;
    private boolean archived;
    private boolean locked = true;
    private boolean printed = false;
}
