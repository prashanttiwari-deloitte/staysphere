package com.example.staysphere.dto;

import lombok.Data;
import java.util.List;

@Data
public class BookingResponse {
    private Long id;
    private String GuestName;
    private String checkInDate;
    private String checkOutDate;
    private Double totalAmount;
    private String bookingDate;
    private List<Long> roomIds;
    private String status;
}
