package com.example.staysphere.repository;

import com.example.staysphere.entity.InventoryHold;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface InventoryHoldRepository extends JpaRepository<InventoryHold, Long> {
    // Find holds for a room that overlap with a given date range
    // (holdEndDate > startDate) AND (holdStartDate < endDate) AND (expiresAt < currentTime)
    List<InventoryHold> findByRoomIdAndHoldEndDateGreaterThanAndHoldStartDateLessThanAndExpiresAtLessThan(
        Long roomId, String startDate, String endDate, String currentTime
    );
}
