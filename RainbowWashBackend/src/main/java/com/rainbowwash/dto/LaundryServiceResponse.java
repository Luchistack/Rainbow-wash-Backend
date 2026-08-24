package com.rainbowwash.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.math.BigDecimal;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class LaundryServiceResponse {
    private Long id;
    private String name;
    private String description;
    private BigDecimal price;
    private String category;
    private int stock;
    private boolean available;
}