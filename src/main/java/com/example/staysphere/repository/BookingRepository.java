package com.example.staysphere.repository;
import org.springframework.data.jpa.repository.JpaRepository;
import com.example.staysphere.entity.Booking;

public interface BookingRepository  extends JpaRepository<Booking, Long> {

}
