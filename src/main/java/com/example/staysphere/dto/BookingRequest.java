package com.example.staysphere.dto;

import lombok.Data;

@Data
public class BookingRequest {
    private Long guestId;
    private String checkInDate;
    private String checkOutDate;
    private Double totalAmount;
    private String bookingDate;
    private Long[] roomIds;
}
