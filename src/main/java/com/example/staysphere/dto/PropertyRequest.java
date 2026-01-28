package com.example.staysphere.dto;
import com.example.staysphere.entity.Room;

import lombok.Data;
import java.util.List;
@Data
public class PropertyRequest {
    String name;
    String address;
    List<Room>rooms;

}
