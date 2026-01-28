package com.example.staysphere.dto;
import lombok.Data;
import java.util.List;

@Data
public class PropertyResponse {

    private Long propertyId;
    private String propertyName;
    private String propertyAddress;
    List<RoomResponse> rooms;
}
