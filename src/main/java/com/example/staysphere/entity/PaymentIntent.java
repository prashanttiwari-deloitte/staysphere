package com.example.staysphere.entity;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Data
public class PaymentIntent {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "booking_id")
    private Booking booking;

    private Double amount;

    @Enumerated(EnumType.STRING)
    private Status status;

    private String createdAt;
    private String updatedAt;

    private String idempotencyKey;

    public enum Status {
        AUTHORIZED,
        CAPTURED,
        FAILED,
        CANCELLED
    }
}
