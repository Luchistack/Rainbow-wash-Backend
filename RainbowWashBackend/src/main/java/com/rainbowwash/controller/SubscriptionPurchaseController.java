package com.rainbowwash.controller;

import com.rainbowwash.dto.SubscriptionPurchasePaymentUpdateRequest;
import com.rainbowwash.dto.SubscriptionPurchaseRequest;
import com.rainbowwash.dto.SubscriptionPurchaseResponse;
import com.rainbowwash.service.SubscriptionPurchaseService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/subscription-purchases")
@PreAuthorize("hasAnyRole('STAFF', 'MANAGER', 'ADMIN')")
public class SubscriptionPurchaseController {

    private final SubscriptionPurchaseService service;

    public SubscriptionPurchaseController(SubscriptionPurchaseService service) {
        this.service = service;
    }

    @GetMapping
    public ResponseEntity<?> getAllPurchases() {
        try {
            return ResponseEntity.ok(service.getAllPurchases());
        } catch (ResponseStatusException exception) {
            return error(exception);
        }
    }

    @PostMapping
    public ResponseEntity<?> createPurchase(
            @Valid @RequestBody SubscriptionPurchaseRequest request) {
        try {
            return ResponseEntity.status(HttpStatus.CREATED)
                .body(service.createPurchase(request));
        } catch (ResponseStatusException exception) {
            return error(exception);
        }
    }

    @PatchMapping("/{id}/payment-status")
    public ResponseEntity<?> updatePaymentStatus(
            @PathVariable Long id,
            @Valid @RequestBody SubscriptionPurchasePaymentUpdateRequest request) {
        try {
            return ResponseEntity.ok(
                service.updatePaymentStatus(id, request.getPaymentStatus())
            );
        } catch (ResponseStatusException exception) {
            return error(exception);
        }
    }

    @PatchMapping("/{id}/printed")
    public ResponseEntity<?> markPrinted(@PathVariable Long id) {
        try {
            return ResponseEntity.ok(service.markPrinted(id));
        } catch (ResponseStatusException exception) {
            return error(exception);
        }
    }

    private ResponseEntity<Map<String, Object>> error(
            ResponseStatusException exception) {
        Map<String, Object> body = Map.of(
            "status", exception.getStatusCode().value(),
            "message", exception.getReason() == null
                ? "Subscription purchase request failed"
                : exception.getReason()
        );
        return ResponseEntity.status(exception.getStatusCode()).body(body);
    }
}
