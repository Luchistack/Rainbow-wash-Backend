package com.rainbowwash.controller;

import com.rainbowwash.dto.LaundryServiceRequest;
import com.rainbowwash.dto.LaundryServiceResponse;
import com.rainbowwash.service.LaundryServiceService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/services")
public class LaundryServiceController {

    private final LaundryServiceService laundryService;

    public LaundryServiceController(LaundryServiceService laundryService) {
        this.laundryService = laundryService;
    }

    // Public — every price on the site (Order Laundry, Services page, the
    // Pricing tab itself) reads from this same endpoint.
    @GetMapping
    public ResponseEntity<List<LaundryServiceResponse>> getAllServices() {
        return ResponseEntity.ok(laundryService.getAllServices());
    }

    // Manager/Admin only from here down — Staff can see prices, not change them.
    @PreAuthorize("hasAnyRole('MANAGER', 'ADMIN')")
    @PostMapping
    public ResponseEntity<LaundryServiceResponse> createService(@Valid @RequestBody LaundryServiceRequest request) {
        return ResponseEntity.ok(laundryService.createService(request));
    }

    @PreAuthorize("hasAnyRole('MANAGER', 'ADMIN')")
    @PutMapping("/{id}")
    public ResponseEntity<LaundryServiceResponse> updateService(
            @PathVariable Long id,
            @Valid @RequestBody LaundryServiceRequest request) {
        return ResponseEntity.ok(laundryService.updateService(id, request));
    }

    @PreAuthorize("hasAnyRole('MANAGER', 'ADMIN')")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteService(@PathVariable Long id) {
        laundryService.deleteService(id);
        return ResponseEntity.noContent().build();
    }
}