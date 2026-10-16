package com.rainbowwash.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;
import java.math.BigDecimal;

@Data
@Entity
@Table(name = "products")
public class Product {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Product name is required")
    @Size(max = 160, message = "Product name must be 160 characters or fewer")
    private String name;
    @NotBlank(message = "Product category is required")
    @Size(max = 100, message = "Category must be 100 characters or fewer")
    private String category;
    @Size(max = 2000, message = "Description must be 2000 characters or fewer")
    private String description;
    @NotNull(message = "Product price is required")
    @DecimalMin(value = "0.00", message = "Product price cannot be negative")
    private BigDecimal price;
    @Min(value = 0, message = "Stock cannot be negative")
    private int stock;
    @Pattern(regexp = "(?i)^(Active|Inactive)$", message = "Status must be Active or Inactive")
    private String status; // "Active" or "Inactive"
}