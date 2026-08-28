package com.rainbowwash.repository;

import com.rainbowwash.model.CleaningBooking;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CleaningBookingRepository extends JpaRepository<CleaningBooking, Long> {
}