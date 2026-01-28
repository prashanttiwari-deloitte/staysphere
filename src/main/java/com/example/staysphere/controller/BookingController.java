package com.example.staysphere.controller;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.http.ResponseEntity;
import java.util.List;
import com.example.staysphere.entity.Booking;
import com.example.staysphere.repository.BookingRepository;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.PostMapping;
import com.example.staysphere.dto.BookingRequest;
import com.example.staysphere.dto.BookingResponse;
import com.example.staysphere.service.BookingService;
import com.example.staysphere.repository.IdempotencyRecordRepository;
import com.example.staysphere.entity.IdempotencyRecord;
import org.springframework.web.bind.annotation.RequestHeader;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.util.Base64;
import static java.time.Instant.now;

@RestController
@RequestMapping("/api/bookings")
public class BookingController {

    @Autowired
    private BookingRepository bookingRepository;

    @Autowired
    private BookingService bookingService;

    @Autowired
    private IdempotencyRecordRepository idempotencyRecordRepository;

    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<Booking>> getAllBookings(){
        List<Booking> bookings = bookingRepository.findAll();
      return ResponseEntity.ok(bookings);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Booking> getBookingById(@PathVariable Long id) {
        Optional<Booking> booking = bookingRepository.findById(id);
        return booking.map(ResponseEntity::ok)
                       .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping("/create")
    public ResponseEntity<BookingResponse> createBooking(
            @RequestBody BookingRequest bookingRequest,
            @RequestHeader(value = "Idempotency-Key", required = false) String idempotencyKey) {
        try {
            // Compute a hash of the request for conflict detection
            String requestHash = hashBookingRequest(bookingRequest);

            if (idempotencyKey != null && !idempotencyKey.isEmpty()) {
                Optional<IdempotencyRecord> existing = idempotencyRecordRepository.findByIdempotencyKey(idempotencyKey);
                if (existing.isPresent()) {
                    IdempotencyRecord record = existing.get();
                    // If the request hash matches, return the stored response
                    if (requestHash.equals(record.getRequestHash())) {
                        BookingResponse response = deserializeResponse(record.getResponseBody());
                        return ResponseEntity.ok(response);
                    } else {
                        // Conflict: same key, different request
                        return ResponseEntity.status(409).build();
                    }
                }
            }

            BookingResponse response = bookingService.doBooking(bookingRequest);

            if (idempotencyKey != null && !idempotencyKey.isEmpty()) {
                IdempotencyRecord record = new IdempotencyRecord();
                record.setIdempotencyKey(idempotencyKey);
                record.setRequestHash(requestHash);
                record.setResponseBody(serializeResponse(response));
                record.setCreatedAt(now().toString());
                idempotencyRecordRepository.save(record);
            }

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.status(500).build();
        }
    }

    // Simple hash function for BookingRequest 
    private String hashBookingRequest(BookingRequest req) throws Exception {
        MessageDigest digest = MessageDigest.getInstance("SHA-256");
        String raw = req.toString(); // Assumes BookingRequest.toString() is deterministic
        byte[] hash = digest.digest(raw.getBytes(StandardCharsets.UTF_8));
        return Base64.getEncoder().encodeToString(hash);
    }

    // Simple (de)serialization for BookingResponse (can use Jackson for production)
    private String serializeResponse(BookingResponse resp) throws Exception {
        return resp.toString(); // Replace with JSON serialization if needed
    }
    private BookingResponse deserializeResponse(String str) throws Exception {
        // Replace with JSON deserialization if needed
        BookingResponse resp = new BookingResponse();
        resp.setStatus(str); // Dummy logic for placeholder
        return resp;
    }
}
