package com.rainbowwash.dto;

import com.rainbowwash.model.OrderStatus;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class OrderResponse {
    private Long id;
    private Long userId;
    private String customerName;
    private BigDecimal totalAmount;
    private OrderStatus status;
    private String deliveryAddress;
    private LocalDateTime createdAt;
    private List<ItemDetail> items;

    @Data
    public static class ItemDetail {
        private String serviceName;
        private int quantity;
        private BigDecimal subTotal;
    }
}