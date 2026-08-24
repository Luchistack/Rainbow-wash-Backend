package com.rainbowwash.repository;

import com.rainbowwash.model.CleaningBooking;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface CleaningBookingRepository extends JpaRepository<CleaningBooking, Long> {
    List<CleaningBooking> findByArchivedFalse();
}