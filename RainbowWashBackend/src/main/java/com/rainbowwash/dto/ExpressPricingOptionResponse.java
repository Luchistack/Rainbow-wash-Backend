package com.rainbowwash.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ExpressPricingOptionResponse {
    private Long id;
    private String turnaround;
    private String pricingUnit;
    private BigDecimal price;
    private boolean available;
}
