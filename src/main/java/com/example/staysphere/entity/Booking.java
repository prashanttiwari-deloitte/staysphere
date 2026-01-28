package com.example.staysphere.entity;

import jakarta.annotation.Generated;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import lombok.Data;
import jakarta.persistence.JoinColumn;
import java.util.List;

@Entity
@Data
public class Booking {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name="guest_id")
    private User guest;

   @OneToMany(mappedBy = "booking", cascade = CascadeType.DETACH)
   private List<Room> roomsBooked;

    private String checkInDate;
    private String checkOutDate;
    private Double totalAmount;
    private String bookingDate;

    @Enumerated(EnumType.STRING)
    private Status status;    

    
    private String statusChangedAt;

    public enum Status {
        REQUESTED,
        CONFIRMED,
        BOOKED,
        CHECKED_IN,
        CHECKED_OUT,
        CANCELLED,
        COMPLETED,
        FAILED
    }

}
