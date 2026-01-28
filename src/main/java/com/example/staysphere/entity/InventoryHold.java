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

    
    private String holdStartDate;
    private String holdEndDate;

    // when the hold was created
    private String createdAt;

   //when the hold expires
    private String expiresAt;

  
    @ManyToOne
    @JoinColumn(name = "booking_id")
    private Booking booking;
}
