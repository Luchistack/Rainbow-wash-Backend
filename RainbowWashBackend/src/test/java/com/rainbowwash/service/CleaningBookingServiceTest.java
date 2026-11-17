package com.rainbowwash.service;

import com.rainbowwash.dto.CleaningBookingRequest;
import com.rainbowwash.model.CleaningBooking;
import com.rainbowwash.repository.CleaningBookingRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.access.AccessDeniedException;
import com.rainbowwash.dto.CleaningBookingUpdateRequest;

@ExtendWith(MockitoExtension.class)
class CleaningBookingServiceTest {

    @Mock
    private CleaningBookingRepository cleaningBookingRepository;

    @InjectMocks
    private CleaningBookingService cleaningBookingService;

    private CleaningBookingRequest request;

    @BeforeEach
    void setUp() {
        request = new CleaningBookingRequest();
        request.setService("Deep Cleaning");
        request.setSize("2 Bedroom");
        request.setDate("2026-08-25");
        request.setTime("10:00 AM");
        request.setAddress("10 Tech Street, Lagos");
        request.setPayType("Card");
        request.setPayable(BigDecimal.valueOf(20000.00));
        request.setFullName("Faith Dike");
        request.setPhone("08011112222");
        request.setEmail("faith@rainbowwash.com");
    }

    @Test
    void createBooking_ShouldSaveSuccessfullyAndGenerateReferenceId() {
        // Arrange
        when(cleaningBookingRepository.save(any(CleaningBooking.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        // Act
        CleaningBooking savedBooking = cleaningBookingService.createBooking(request);

        // Assert
        assertNotNull(savedBooking);
        assertNotNull(savedBooking.getReferenceId());
        assertTrue(savedBooking.getReferenceId().startsWith("CLN-"));
        assertEquals("Deep Cleaning", savedBooking.getService());
        assertEquals("Faith Dike", savedBooking.getFullName());
        assertEquals("Pending", savedBooking.getPaymentStatus());
        assertEquals("Pending Quote", savedBooking.getStatus());
        assertFalse(savedBooking.isArchived());

        // Verify repository interaction
        verify(cleaningBookingRepository, times(1)).save(any(CleaningBooking.class));
    }

    @Test
    void getAllBookings_ShouldReturnAllBookings() {
        // Arrange — includes both archived and non-archived, since History needs
        // to see everything; Today/History filtering now happens client-side.
        CleaningBooking booking = new CleaningBooking();
        booking.setId(1L);
        booking.setService("Standard Cleaning");
        booking.setArchived(false);

        when(cleaningBookingRepository.findAll()).thenReturn(List.of(booking));

        // Act
        List<CleaningBooking> bookings = cleaningBookingService.getAllBookings();

        // Assert
        assertNotNull(bookings);
        assertEquals(1, bookings.size());
        assertEquals("Standard Cleaning", bookings.get(0).getService());

        verify(cleaningBookingRepository, times(1)).findAll();
    }

    @AfterEach
    void clearSecurityContext() {
        SecurityContextHolder.clearContext();
    }

    private void authenticateAs(String role) {
        SecurityContextHolder.getContext().setAuthentication(
                new UsernamePasswordAuthenticationToken(
                        "dashboard-user", "password",
                        List.of(new SimpleGrantedAuthority(role))));
    }

    @Test
    void createBooking_appliesDiscountForStaffManagerAndAdmin() {
        when(cleaningBookingRepository.save(any(CleaningBooking.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        for (String role : List.of("ROLE_STAFF", "ROLE_MANAGER", "ROLE_ADMIN")) {
            authenticateAs(role);
            request.setDiscountPercentage(new BigDecimal("5"));

            CleaningBooking result = cleaningBookingService.createBooking(request);

            assertEquals(0, new BigDecimal("5").compareTo(result.getDiscountPercentage()),
                    role + " should be allowed to apply a cleaning discount");
            assertEquals(0, new BigDecimal("1000.00").compareTo(result.getDiscountAmount()),
                    role + " should receive the correct cleaning discount amount");
            assertEquals(0, new BigDecimal("20000.00").compareTo(result.getPayable()),
                    role + " should leave the original payable unchanged");
        }
    }

    @Test
    void createBooking_rejectsDiscountWithoutAuthenticatedDashboardUser() {
        request.setDiscountPercentage(new BigDecimal("5"));

        assertThrows(AccessDeniedException.class,
                () -> cleaningBookingService.createBooking(request));

        verify(cleaningBookingRepository, never()).save(any(CleaningBooking.class));
    }

    @Test
    void updateBooking_recalculatesDiscountWhenPayableChanges() {
        CleaningBooking existing = new CleaningBooking();
        existing.setId(10L);
        existing.setPayable(new BigDecimal("20000"));
        existing.setDiscountPercentage(new BigDecimal("5"));
        existing.setDiscountAmount(new BigDecimal("1000"));

        when(cleaningBookingRepository.findById(10L)).thenReturn(Optional.of(existing));
        when(cleaningBookingRepository.save(any(CleaningBooking.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        CleaningBookingUpdateRequest update = new CleaningBookingUpdateRequest();
        update.setPayable(new BigDecimal("30000"));

        CleaningBooking result = cleaningBookingService.updateBooking(10L, update);

        assertEquals(0, new BigDecimal("30000").compareTo(result.getPayable()));
        assertEquals(0, new BigDecimal("1500.00").compareTo(result.getDiscountAmount()));
    }

    @Test
    void updateBooking_preservesDiscountWhenOnlyStatusChanges() {
        CleaningBooking existing = new CleaningBooking();
        existing.setId(11L);
        existing.setPayable(new BigDecimal("20000"));
        existing.setDiscountPercentage(new BigDecimal("5"));
        existing.setDiscountAmount(new BigDecimal("1000"));

        when(cleaningBookingRepository.findById(11L)).thenReturn(Optional.of(existing));
        when(cleaningBookingRepository.save(any(CleaningBooking.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        CleaningBookingUpdateRequest update = new CleaningBookingUpdateRequest();
        update.setStatus("Confirmed");

        CleaningBooking result = cleaningBookingService.updateBooking(11L, update);

        assertEquals("Confirmed", result.getStatus());
        assertEquals(0, new BigDecimal("1000").compareTo(result.getDiscountAmount()));
    }
}
