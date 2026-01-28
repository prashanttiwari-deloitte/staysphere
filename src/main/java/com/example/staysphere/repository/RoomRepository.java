package com.example.staysphere.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import jakarta.persistence.LockModeType;

import com.example.staysphere.entity.Room;
import java.util.Optional;

public interface RoomRepository extends JpaRepository<Room, Long> {
    // Additional query methods can be defined here if needed

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    Optional<Room> findById(Long id);
}
