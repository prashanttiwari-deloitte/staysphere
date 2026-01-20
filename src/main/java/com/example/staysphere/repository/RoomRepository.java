package com.example.staysphere.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.staysphere.entity.Room;

public interface RoomRepository extends JpaRepository<Room, Long> {
    // Additional query methods can be defined here if needed
}
