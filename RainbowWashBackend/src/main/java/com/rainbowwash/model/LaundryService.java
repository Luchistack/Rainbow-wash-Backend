package com.rainbowwash.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.math.BigDecimal;

@Entity
@Table(name = "laundry_services", uniqueConstraints = @UniqueConstraint(columnNames = {"name", "category"}))
@Data
@NoArgsConstructor
@AllArgsConstructor
public class LaundryService {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Unique per category, not globally — "Wash & Dry" needs to exist once
    // under Self Wash and separately once under Staff Wash, for example. The
    // real uniqueness constraint now lives on (name, category) together, via
    // the @Table annotation above.
    @Column(nullable = false)
    private String name;

    private String description;

    @Column(nullable = false)
    private BigDecimal price;

    // Nullable — only Dry Cleaning and Shoe Care items use these. Self-Wash,
    // Staff-Wash, Express, cleaning-service sizes and add-on products just use
    // `price` alone. Reusing one flat entity for every pricing category (instead
    // of building six near-identical ones) keeps this all in one real,
    // backend-synced place with the CRUD machinery already built and tested.
    private BigDecimal deepPrice;
    private BigDecimal repairPrice; // Shoe Care only

    @Column(nullable = false)
    private String category;

    @Column(nullable = false)
    private int stock = 5; // Matches your frontend rule: "New stock always starts at 5 units or more"

    @Column(nullable = false)
    private boolean available = true;
}