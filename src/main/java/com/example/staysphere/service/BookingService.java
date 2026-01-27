package com.example.staysphere.service;

import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import com.example.staysphere.dto.BookingRequest;
import com.example.staysphere.dto.BookingResponse;
import com.example.staysphere.entity.Booking;
import com.example.staysphere.repository.BookingRepository;
import com.example.staysphere.repository.RoomRepository;
import com.example.staysphere.repository.UserRepository;
import com.example.staysphere.entity.User;
import com.example.staysphere.entity.Booking.Status;
import com.example.staysphere.entity.InventoryHold;
import com.example.staysphere.repository.InventoryHoldRepository;

import java.util.Optional;
import com.example.staysphere.entity.Room;
import java.util.List;
import java.util.ArrayList;
import static java.time.Instant.now;



@Service
public class BookingService {

    @Autowired
    private BookingRepository bookingRepository;
    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RoomRepository  roomRepository;

    @Autowired
    private InventoryHoldRepository inventoryHoldRepository;

    @Transactional
    public BookingResponse doBooking (BookingRequest bookingRequest) {
        Booking booking = new Booking();
        Optional<User> guest = userRepository.findById(bookingRequest.getGuestId());
        // Check for overlapping holds for each requested room and date range
        String checkInDate = bookingRequest.getCheckInDate();
        String checkOutDate = bookingRequest.getCheckOutDate();
        boolean allRoomsAvailable = true;
        List<Room> rooms = new ArrayList<>();
        //List<InventoryHold> holdsToCreate = new ArrayList<>();
        for(Long roomId : bookingRequest.getRoomIds()){
            // Use pessimistic lock to prevent concurrent modifications
            Optional<Room> roomOpt = roomRepository.findById(roomId);
            if(roomOpt.isPresent()){
                Room room = roomOpt.get();
                // Check for overlapping holds
                List<InventoryHold> overlappingHolds = inventoryHoldRepository
                    .findByRoomIdAndHoldEndDateGreaterThanAndHoldStartDateLessThanAndExpiresAtLessThan(
                        roomId, checkInDate, checkOutDate, now().toString()
                    );
                if(overlappingHolds != null && !overlappingHolds.isEmpty()){
                    allRoomsAvailable = false;
                    break;
                }
                rooms.add(room);
            }
        }
        if(!guest.isPresent() || rooms.isEmpty() || !allRoomsAvailable){
            booking.setStatus(Status.FAILED);
            booking.setStatusChangedAt(now().toString());
        }else{
            booking.setGuest(guest.get());
            booking.setRoomsBooked(rooms);
            booking.setCheckInDate(checkInDate);
            booking.setCheckOutDate(checkOutDate);  
            booking.setTotalAmount(bookingRequest.getTotalAmount());
            booking.setStatus(Status.REQUESTED);
            booking.setStatusChangedAt(now().toString());
            booking.setBookingDate(bookingRequest.getBookingDate());
            bookingRepository.save(booking);

            // Create InventoryHold for each room
            String now = now().toString();
            String expiresAt = now().plusSeconds(15 * 60).toString(); // 15 minutes temperary hold
            for(Room room : rooms){
                InventoryHold hold = new InventoryHold();
                hold.setRoom(room);
                hold.setHoldStartDate(checkInDate);
                hold.setHoldEndDate(checkOutDate);
                hold.setCreatedAt(now);
                hold.setExpiresAt(expiresAt);
                hold.setBooking(booking);
                inventoryHoldRepository.save(hold);
            }
        }
        BookingResponse bookingResponse = generateBookingResponse(booking);
        return bookingResponse;

    }

    private BookingResponse generateBookingResponse (Booking booking){
        BookingResponse response = new BookingResponse();
        if(booking.getStatus() == Status.FAILED){
            response.setStatus("FAILED");
            return response;
        }
        response.setId(booking.getId());
        response.setGuestName(booking.getGuest().getName());
        response.setCheckInDate(booking.getCheckInDate());
        response.setCheckOutDate(booking.getCheckOutDate());
        response.setBookingDate(booking.getBookingDate());
        response.setStatus(booking.getStatus().toString()); 
        Double totalAmount = booking.getRoomsBooked().stream()
            .mapToDouble(Room::getPrice).sum();
        response.setTotalAmount(totalAmount);
        List<Long> roomIds = booking.getRoomsBooked().stream()
        .map(Room::getId).toList();
        response.setRoomIds(roomIds);
        return response;

    }

}
