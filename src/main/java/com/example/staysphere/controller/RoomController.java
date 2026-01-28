package com.example.staysphere.controller;

import org.apache.catalina.connector.Response;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.security.access.prepost.PreAuthorize;

import com.example.staysphere.entity.Room;
import com.example.staysphere.repository.RoomRepository;
import com.example.staysphere.service.PropertyService;
import com.example.staysphere.dto.RoomResponse;


import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/rooms")
public class RoomController {

    @Autowired
    private RoomRepository roomRepository;

   

    @GetMapping("getAll")
    //@PreAuthorize("hasRole('ADMIN') or hasRole('PROPERTY_MANAGER')")
    public List<RoomResponse> getAllRooms() {
        return roomRepository.findAll().stream().map(room -> {
            RoomResponse roomResponse = RoomResponse.builder()
                                        .roomId(room.getId())
                                        .roomType(room.getType())
                                        .price(room.getPrice())
                                        .availability(room.isAvailability())
                                        .propertyName(room.getProperty().getName())
                                        .build();
            return roomResponse;
        }).toList();

    }

    @GetMapping("get/{id}")
    //@PreAuthorize("hasRole('ADMIN') or hasRole('PROPERTY_MANAGER')")
    public ResponseEntity<RoomResponse> getRoomById(@PathVariable Long id) {
        Optional<Room> room = roomRepository.findById(id);
        if(!room.isPresent()){
            return ResponseEntity.notFound().build();
        }
        RoomResponse roomResponse = RoomResponse.builder()
                                    .roomId(room.get().getId())
                                    .roomType(room.get().getType())
                                    .price(room.get().getPrice())
                                    .availability(room.get().isAvailability())
                                    .propertyName(room.get().getProperty().getName())
                                    .build();
        return ResponseEntity.ok(roomResponse);
    }

    @PostMapping("/create")
    @PreAuthorize("hasRole('ADMIN') or hasRole('PROPERTY_MANAGER')")
    public Room createRoom(@RequestBody Room room) {
        return roomRepository.save(room);
    }

    @PutMapping("update/{id}")
    @PreAuthorize("hasRole('ADMIN') or hasRole('PROPERTY_MANAGER')")
    public ResponseEntity<Room> updateRoom(@PathVariable Long id, @RequestBody Room roomDetails) {
        return roomRepository.findById(id)
            .map(room -> {
                room.setType(roomDetails.getType());
                room.setPrice(roomDetails.getPrice());
                room.setProperty(roomDetails.getProperty());
                return ResponseEntity.ok(roomRepository.save(room));
            })
            .orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("delete/{id}")
    @PreAuthorize("hasRole('ADMIN') or hasRole('PROPERTY_MANAGER')")
    public ResponseEntity<Void> deleteRoom(@PathVariable Long id) {
        return roomRepository.findById(id)
            .map(room -> {
                roomRepository.delete(room);
                return ResponseEntity.noContent().<Void>build();
            })
            .orElse(ResponseEntity.notFound().build());
    }
}
