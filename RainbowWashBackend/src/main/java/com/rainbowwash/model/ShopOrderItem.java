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
@Table(name = "shop_order_items")
public class ShopOrderItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Same reasoning as OrderItem.order — excluded from JSON and from
    // Lombok's equals/hashCode/toString to break the ShopOrder <-> items
    // circular reference that was crashing every shop-orders response.
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "shop_order_id", nullable = false)
    @JsonIgnore
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private ShopOrder shopOrder;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private int qty;

    @Column(nullable = false)
    private BigDecimal price;
}