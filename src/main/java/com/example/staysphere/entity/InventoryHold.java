package com.example.staysphere.entity;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Data
public class InventoryHold {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "room_id")
    private Room room;

    // Start and end date of the hold (ISO 8601 string for simplicity)
    private String holdStartDate;
    private String holdEndDate;

    // When the hold was created
    private String createdAt;

    // When the hold expires (TTL)
    private String expiresAt;

    // Associated booking, nullable (set when booking is confirmed)
    @ManyToOne
    @JoinColumn(name = "booking_id")
    private Booking booking;
}
