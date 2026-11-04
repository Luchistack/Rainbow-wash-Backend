package com.rainbowwash.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SubscriptionPlanResponse {
    private Long id;
    private String name;
    private BigDecimal price;
    private String duration;
    private String covers;
    private boolean active;
}
