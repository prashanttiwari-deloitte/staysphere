package com.example.staysphere.service;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.example.staysphere.repository.PropertyRepository;
import com.example.staysphere.dto.PropertyResponse; 
import java.util.List;
import com.example.staysphere.dto.RoomResponse;
import com.example.staysphere.entity.Room;

@Service
public class PropertyService {

    @Autowired
    private PropertyRepository propertyRepository;

    public List<PropertyResponse> getAllProperties() {
        try{
            List<PropertyResponse> responses = propertyRepository.findAll().stream()
            .map(property -> {
                PropertyResponse response = new PropertyResponse();
                response.setPropertyId(property.getId());
                response.setPropertyName(property.getName());
                response.setPropertyAddress(property.getAddress());
                List<RoomResponse> roomResponses = property.getRooms().stream().map(room -> {
                    RoomResponse roomResponse = RoomResponse.builder()
                                                .roomId(room.getId())
                                                .roomType(room.getType())
                                                .price(room.getPrice())
                                                .availability(room.isAvailability())
                                                .propertyName(property.getName())
                                                .build();
                    return roomResponse;
                }).toList();
                response.setRooms(roomResponses);
                return response;    
            }).toList();

            return responses;
        }catch(Exception e){
            throw new RuntimeException("Error fetching properties", e);
        }
    }
}
