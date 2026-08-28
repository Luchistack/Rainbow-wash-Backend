package com.rainbowwash.dto;

import lombok.Data;

@Data
public class ShopOrderUpdateRequest {
    private String status;
    private String paymentStatus;
    private Boolean archived;
    private Boolean printed;
}
