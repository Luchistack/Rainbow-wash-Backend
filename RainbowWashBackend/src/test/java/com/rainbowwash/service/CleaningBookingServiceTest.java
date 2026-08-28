package com.rainbowwash.service;

import com.rainbowwash.dto.CleaningBookingRequest;
import com.rainbowwash.model.CleaningBooking;
import com.rainbowwash.repository.CleaningBookingRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

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
}