package com.example.staysphere.repository;

import com.example.staysphere.entity.InventoryHold;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface InventoryHoldRepository extends JpaRepository<InventoryHold, Long> {
    // Find holds for a room that overlap with a given date range
    // (holdEndDate > startDate) AND (holdStartDate < endDate)
    java.util.List<InventoryHold> findByRoomIdAndHoldEndDateGreaterThanAndHoldStartDateLessThan(
        Long roomId, String startDate, String endDate
    );
}
