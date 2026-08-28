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
@Table(name = "shop_orders")
public class ShopOrder {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String referenceId;

    @OneToMany(mappedBy = "shopOrder", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ShopOrderItem> items = new ArrayList<>();

    private String fullName;
    private String phone;
    private String mode; // "pickup" | "delivery"

    @Column(nullable = false)
    private BigDecimal total;

    private String status; // "Received" | "Packed" | "Out for Delivery" | "Completed"
    private String paymentStatus; // "Pending" | "Sent" | "Confirmed"

    private String createdBy; // staff name for walk-in sales, null for customer web orders

    private LocalDateTime placedAt;
    private boolean archived;
    private boolean locked = true;
    private boolean printed = false;
}
