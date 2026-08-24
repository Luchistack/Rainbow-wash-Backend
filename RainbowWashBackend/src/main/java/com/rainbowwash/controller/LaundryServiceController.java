package com.rainbowwash.controller;

import com.rainbowwash.dto.LaundryServiceRequest;
import com.rainbowwash.dto.LaundryServiceResponse;
import com.rainbowwash.service.LaundryServiceService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/services")
public class LaundryServiceController {

    private final LaundryServiceService laundryService;

    public LaundryServiceController(LaundryServiceService laundryService) {
        this.laundryService = laundryService;
    }

    @GetMapping
    public ResponseEntity<List<LaundryServiceResponse>> getAllServices() {
        return ResponseEntity.ok(laundryService.getAllServices());
    }

    @PostMapping
    public ResponseEntity<LaundryServiceResponse> createService(@Valid @RequestBody LaundryServiceRequest request) {
        return ResponseEntity.ok(laundryService.createService(request));
    }

    @PutMapping("/{id}")
    public ResponseEntity<LaundryServiceResponse> updateService(
            @PathVariable Long id,
            @Valid @RequestBody LaundryServiceRequest request) {
        return ResponseEntity.ok(laundryService.updateService(id, request));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteService(@PathVariable Long id) {
        laundryService.deleteService(id);
        return ResponseEntity.noContent().build();
    }
}