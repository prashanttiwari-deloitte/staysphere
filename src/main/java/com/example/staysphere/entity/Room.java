package com.example.staysphere.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "rooms")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Room {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String roomNumber;

    private String type;

    private Double price;

    private boolean availability;

    // Many rooms belong to one property
    @ManyToOne
    @JoinColumn(name = "property_id")
    private Property property;

    @ManyToMany
    @JoinColumn(name="booking_id")
    private Booking booking;
}
