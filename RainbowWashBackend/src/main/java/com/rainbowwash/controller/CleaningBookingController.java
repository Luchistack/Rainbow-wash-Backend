package com.rainbowwash.controller;

import com.rainbowwash.dto.CleaningBookingRequest;
import com.rainbowwash.model.CleaningBooking;
import com.rainbowwash.service.CleaningBookingService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
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
}