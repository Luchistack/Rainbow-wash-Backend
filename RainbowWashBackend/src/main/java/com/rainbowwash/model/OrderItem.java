package com.rainbowwash.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@Entity
@Table(name = "order_items")
public class OrderItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // The back-reference to the parent Order is needed for the database
    // relationship, but must never be serialized to JSON or included in
    // Lombok's generated equals/hashCode/toString — Order already embeds its
    // full list of OrderItems, so Order -> items -> order -> items -> ...
    // would recurse forever. This was the actual cause of orders and shop
    // orders silently failing to save/load: every GET (and every response
    // right after a successful save) crashed trying to serialize this loop,
    // which the frontend correctly treated as a failure.
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "order_id", nullable = false)
    @JsonIgnore
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private Order order;

    @Column(nullable = false)
    private String name;

    // BigDecimal, not int — self/staff wash lines are priced by kg and can be
    // fractional (e.g. 3.5kg).
    @Column(nullable = false)
    private BigDecimal qty;

    private String unit; // "kg" or blank for piece-based items

    @Column(nullable = false)
    private BigDecimal price;
}