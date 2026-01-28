package com.example.staysphere.dto;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class RoomResponse {
    private Long roomId;
    private String roomType;
    private Double price;
    private boolean availability;
    private String propertyName;
}
