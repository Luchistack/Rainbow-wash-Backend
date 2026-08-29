package com.rainbowwash.service;

import com.rainbowwash.dto.CleaningBookingRequest;
import com.rainbowwash.dto.CleaningBookingUpdateRequest;
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
        booking.setLocked(true);
        booking.setPrinted(false);

        return bookingRepository.save(booking);
    }

    public List<CleaningBooking> getAllBookings() {
        return bookingRepository.findAll();
    }

    // Partial update — only the fields staff actually change from the dashboard
    // (status, confirmed price, payment status, archived, printed). Never touches
    // customer-entered fields like name/phone/service, matching the "content is
    // locked after submission" rule from the PRD.
    public CleaningBooking updateBooking(Long id, CleaningBookingUpdateRequest request) {
        CleaningBooking booking = bookingRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Booking not found"));

        if (request.getStatus() != null) booking.setStatus(request.getStatus());
        if (request.getPayable() != null) booking.setPayable(request.getPayable());
        if (request.getPaymentStatus() != null) booking.setPaymentStatus(request.getPaymentStatus());
        if (request.getArchived() != null) booking.setArchived(request.getArchived());
        if (request.getPrinted() != null) booking.setPrinted(request.getPrinted());
        if (request.getPaymentMethod() != null) booking.setPaymentMethod(request.getPaymentMethod());

        return bookingRepository.save(booking);
    }

    public void deleteBooking(Long id) {
        bookingRepository.deleteById(id);
    }
}