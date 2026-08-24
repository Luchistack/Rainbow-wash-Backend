package com.rainbowwash.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.math.BigDecimal;

@Entity
@Table(name = "laundry_services")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class LaundryService {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String name;

    private String description;

    @Column(nullable = false)
    private BigDecimal price;

    @Column(nullable = false)
    private String category;

    @Column(nullable = false)
    private int stock = 5; // Matches your frontend rule: "New stock always starts at 5 units or more"

    @Column(nullable = false)
    private boolean available = true;
}