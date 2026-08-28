package com.rainbowwash.controller;

import com.rainbowwash.dto.CleaningBookingRequest;
import com.rainbowwash.dto.CleaningBookingUpdateRequest;
import com.rainbowwash.model.CleaningBooking;
import com.rainbowwash.service.CleaningBookingService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/bookings")
public class CleaningBookingController {

    private final CleaningBookingService bookingService;

    public CleaningBookingController(CleaningBookingService bookingService) {
        this.bookingService = bookingService;
    }

    @PostMapping
    public ResponseEntity<CleaningBooking> createBooking(@Valid @RequestBody CleaningBookingRequest request) {
        System.out.println("RECEIVED BOOKING REQUEST: " + request);
        return ResponseEntity.ok(bookingService.createBooking(request));
    }

    @GetMapping
    public ResponseEntity<List<CleaningBooking>> getAllBookings() {
        return ResponseEntity.ok(bookingService.getAllBookings());
    }

    // Staff/Manager/Admin only (any authenticated dashboard user) — content edits
    // are further restricted client-side once printed, per the PRD's locking rule.
    @PatchMapping("/{id}")
    public ResponseEntity<CleaningBooking> updateBooking(@PathVariable Long id, @RequestBody CleaningBookingUpdateRequest request) {
        return ResponseEntity.ok(bookingService.updateBooking(id, request));
    }
}