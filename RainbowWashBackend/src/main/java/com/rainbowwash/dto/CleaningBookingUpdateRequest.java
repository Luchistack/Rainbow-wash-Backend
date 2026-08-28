package com.rainbowwash.dto;

import lombok.Data;
import java.math.BigDecimal;

@Data
public class CleaningBookingUpdateRequest {
    private String status;
    private BigDecimal payable;
    private String paymentStatus;
    private Boolean archived;
    private Boolean printed;
}
