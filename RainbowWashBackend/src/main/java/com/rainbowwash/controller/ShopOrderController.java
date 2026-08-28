package com.rainbowwash.controller;

import com.rainbowwash.dto.ShopOrderRequest;
import com.rainbowwash.dto.ShopOrderUpdateRequest;
import com.rainbowwash.model.ShopOrder;
import com.rainbowwash.service.ShopOrderService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/shop-orders")
public class ShopOrderController {

    private final ShopOrderService shopOrderService;

    public ShopOrderController(ShopOrderService shopOrderService) {
        this.shopOrderService = shopOrderService;
    }

    // Public — customers check out from the shop without an account.
    @PostMapping
    public ResponseEntity<ShopOrder> createShopOrder(@Valid @RequestBody ShopOrderRequest request) {
        return ResponseEntity.ok(shopOrderService.createShopOrder(request));
    }

    @GetMapping
    public ResponseEntity<List<ShopOrder>> getAllShopOrders() {
        return ResponseEntity.ok(shopOrderService.getAllShopOrders());
    }

    @PatchMapping("/{id}")
    public ResponseEntity<ShopOrder> updateShopOrder(@PathVariable Long id, @RequestBody ShopOrderUpdateRequest request) {
        return ResponseEntity.ok(shopOrderService.updateShopOrder(id, request));
    }
}
