package com.rainbowwash.model;

import jakarta.persistence.*;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "cleaning_bookings")
public class CleaningBooking {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String referenceId;
    private String service;
    private String size;
    private String bookingDate;
    private String bookingTime;
    private String address;
    private String payType;
    private BigDecimal payable;
    private String transferNote;
    private String paymentStatus;
    private String status; // "Pending Quote", "Confirmed", etc.

    private String fullName;
    private String phone;
    private String email;

    private LocalDateTime placedAt;
    private boolean archived;

    // Locked the moment a booking is created (staff can't edit/delete it after
    // submission, only Manager/Admin can) — printed becomes true only once a
    // real print button is clicked, after which nobody can delete it.
    private boolean locked = true;
    private boolean printed = false;
}