package com.rainbowwash.service;

import com.rainbowwash.dto.CleaningBookingRequest;
import com.rainbowwash.dto.CleaningBookingUpdateRequest;
import com.rainbowwash.model.CleaningBooking;
import com.rainbowwash.repository.CleaningBookingRepository;
import org.springframework.stereotype.Service;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import java.math.BigDecimal;
import java.math.RoundingMode;

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

        BigDecimal discountPercentage = request.getDiscountPercentage() == null
                ? BigDecimal.ZERO : request.getDiscountPercentage();
        BigDecimal discountAmount = BigDecimal.ZERO;

        if (discountPercentage.compareTo(BigDecimal.ZERO) > 0) {
            requireDiscountPermission();
            discountAmount = request.getPayable()
                    .multiply(discountPercentage)
                    .divide(new BigDecimal("100"), 2, RoundingMode.HALF_UP);
        }

        booking.setDiscountPercentage(discountPercentage);
        booking.setDiscountAmount(discountAmount);
        booking.setTransferNote(request.getTransferNote());
        booking.setFullName(request.getFullName());
        booking.setPhone(request.getPhone());
        booking.setEmail(request.getEmail());
        booking.setDescription(request.getDescription());
        booking.setPaymentStatus("Pending");
        booking.setStatus("Pending Quote");
        booking.setPlacedAt(LocalDateTime.now());
        booking.setArchived(false);
        booking.setLocked(true);
        booking.setPrinted(false);

        return bookingRepository.save(booking);
    }

    private void requireDiscountPermission() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        boolean allowed = authentication != null
                && authentication.isAuthenticated()
                && !(authentication instanceof AnonymousAuthenticationToken)
                && authentication.getAuthorities().stream().anyMatch(authority ->
                        authority.getAuthority().equals("ROLE_STAFF")
                                || authority.getAuthority().equals("ROLE_MANAGER")
                                || authority.getAuthority().equals("ROLE_ADMIN"));

        if (!allowed) {
            throw new AccessDeniedException("You do not have permission to apply a discount");
        }
    }

    public List<CleaningBooking> getAllBookings() {
        return bookingRepository.findAll();
    }

    // Manager/Admin can edit all booking details, including records already printed.
    // Omitted fields remain unchanged so existing dashboard partial updates continue to work.
    public CleaningBooking updateBooking(Long id, CleaningBookingUpdateRequest request) {
        CleaningBooking booking = bookingRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Booking not found"));

        if (request.getFullName() != null) booking.setFullName(request.getFullName());
        if (request.getPhone() != null) booking.setPhone(request.getPhone());
        if (request.getEmail() != null) booking.setEmail(request.getEmail());
        if (request.getDescription() != null) booking.setDescription(request.getDescription());

        if (request.getService() != null) booking.setService(request.getService());
        if (request.getSize() != null) booking.setSize(request.getSize());
        if (request.getDate() != null) booking.setBookingDate(request.getDate());
        if (request.getTime() != null) booking.setBookingTime(request.getTime());
        if (request.getAddress() != null) booking.setAddress(request.getAddress());
        if (request.getPayType() != null) booking.setPayType(request.getPayType());
        if (request.getTransferNote() != null) booking.setTransferNote(request.getTransferNote());

        if (request.getPaymentStatus() != null) booking.setPaymentStatus(request.getPaymentStatus());
        if (request.getPaymentMethod() != null) booking.setPaymentMethod(request.getPaymentMethod());
        if (request.getStatus() != null) booking.setStatus(request.getStatus());
        if (request.getArchived() != null) booking.setArchived(request.getArchived());
        if (request.getPrinted() != null) booking.setPrinted(request.getPrinted());

        if (request.getPayable() != null) booking.setPayable(request.getPayable());

        BigDecimal discountPercentage = request.getDiscountPercentage() != null
                ? request.getDiscountPercentage()
                : (booking.getDiscountPercentage() == null ? BigDecimal.ZERO : booking.getDiscountPercentage());

        if (request.getPayable() != null || request.getDiscountPercentage() != null) {
            if (request.getDiscountPercentage() != null
                    && discountPercentage.compareTo(BigDecimal.ZERO) > 0) {
                requireDiscountPermission();
            }

            BigDecimal payable = booking.getPayable() == null
                    ? BigDecimal.ZERO : booking.getPayable();

            BigDecimal discountAmount = payable
                    .multiply(discountPercentage)
                    .divide(new BigDecimal("100"), 2, RoundingMode.HALF_UP);

            booking.setDiscountPercentage(discountPercentage);
            booking.setDiscountAmount(discountAmount);
        }

        return bookingRepository.save(booking);
    }

    public void deleteBooking(Long id) {
        bookingRepository.deleteById(id);
    }
}