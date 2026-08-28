package com.rainbowwash.dto;

import lombok.Data;
import java.math.BigDecimal;

@Data
public class OrderUpdateRequest {
    private String status;
    private BigDecimal total;
    private String paymentStatus;
    private Boolean archived;
    private Boolean printed;
    private String paymentMethod;
}