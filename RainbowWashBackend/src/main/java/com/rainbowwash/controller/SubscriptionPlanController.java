package com.rainbowwash.controller;

import com.rainbowwash.dto.SubscriptionPlanRequest;
import com.rainbowwash.dto.SubscriptionPlanResponse;
import com.rainbowwash.service.SubscriptionPlanService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/subscription-plans")
public class SubscriptionPlanController {

    private final SubscriptionPlanService service;

    public SubscriptionPlanController(SubscriptionPlanService service) {
        this.service = service;
    }

    @GetMapping
    public ResponseEntity<List<SubscriptionPlanResponse>> getActivePlans() {
        return ResponseEntity.ok(service.getActivePlans());
    }

    @PreAuthorize("hasAnyRole('MANAGER', 'ADMIN')")
    @GetMapping("/manage")
    public ResponseEntity<List<SubscriptionPlanResponse>> getAllPlans() {
        return ResponseEntity.ok(service.getAllPlans());
    }

    @PreAuthorize("hasAnyRole('MANAGER', 'ADMIN')")
    @PostMapping
    public ResponseEntity<SubscriptionPlanResponse> createPlan(
            @Valid @RequestBody SubscriptionPlanRequest request) {
        return ResponseEntity.ok(service.createPlan(request));
    }

    @PreAuthorize("hasAnyRole('MANAGER', 'ADMIN')")
    @PutMapping("/{id}")
    public ResponseEntity<SubscriptionPlanResponse> updatePlan(
            @PathVariable Long id,
            @Valid @RequestBody SubscriptionPlanRequest request) {
        return ResponseEntity.ok(service.updatePlan(id, request));
    }

    @PreAuthorize("hasAnyRole('MANAGER', 'ADMIN')")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletePlan(@PathVariable Long id) {
        service.deletePlan(id);
        return ResponseEntity.noContent().build();
    }
}
