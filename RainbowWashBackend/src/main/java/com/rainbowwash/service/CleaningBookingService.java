package com.rainbowwash.service;

import com.rainbowwash.dto.CleaningBookingRequest;
import com.rainbowwash.model.CleaningBooking;
import com.rainbowwash.repository.CleaningBookingRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
public class CleaningBookingService {

    private final CleaningBookingRepository bookingRepository;

    public CleaningBookingService(CleaningBookingRepository bookingRepository) {
        this.bookingRepository = bookingRepository;
    }

    public CleaningBooking createBooking(CleaningBookingRequest request) {
        CleaningBooking booking = new CleaningBooking();
        booking.setReferenceId("CLN-" + UUID.randomUUID().toString().substring(0, 6).toUpperCase());
        booking.setService(request.getService());
        booking.setSize(request.getSize());
        booking.setBookingDate(request.getDate());
        booking.setBookingTime(request.getTime());
        booking.setAddress(request.getAddress());
        booking.setPayType(request.getPayType());
        booking.setPayable(request.getPayable());
        booking.setTransferNote(request.getTransferNote());
        booking.setFullName(request.getFullName());
        booking.setPhone(request.getPhone());
        booking.setEmail(request.getEmail());
        booking.setPaymentStatus("Pending");
        booking.setStatus("Pending Quote");
        booking.setPlacedAt(LocalDateTime.now());
        booking.setArchived(false);

        return bookingRepository.save(booking);
    }

    public List<CleaningBooking> getAllBookings() {
        return bookingRepository.findByArchivedFalse();
    }
}