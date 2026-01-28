package com.example.staysphere.controller;

import com.example.staysphere.entity.PaymentIntent;
import com.example.staysphere.entity.Booking;
import com.example.staysphere.repository.PaymentIntentRepository;
import com.example.staysphere.repository.BookingRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.ResponseEntity;
import static java.time.Instant.now;

import java.util.Optional;

@RestController
@RequestMapping("/api/payments")
public class PaymentController {

    @Autowired
    private PaymentIntentRepository paymentIntentRepository;

    @Autowired
    private BookingRepository bookingRepository;

    // Authorize payment (idempotent)
    @PostMapping("/authorize")
    public ResponseEntity<PaymentIntent> authorizePayment(
            @RequestParam Long bookingId,
            @RequestParam Double amount,
            @RequestHeader(value = "Idempotency-Key", required = false) String idempotencyKey) {

        if (idempotencyKey != null && !idempotencyKey.isEmpty()) {
            Optional<PaymentIntent> existing = paymentIntentRepository.findByIdempotencyKey(idempotencyKey);
            if (existing.isPresent()) {
                PaymentIntent intent = existing.get();
                // If booking and amount match, return the stored intent
                if (intent.getBooking().getId().equals(bookingId) && intent.getAmount().equals(amount)) {
                    return ResponseEntity.ok(intent);
                } else {
                    // Conflict: same key, different request
                    return ResponseEntity.status(409).build();
                }
            }
        }

        Optional<Booking> bookingOpt = bookingRepository.findById(bookingId);
        if (bookingOpt.isEmpty()) {
            return ResponseEntity.badRequest().build();
        }

        PaymentIntent intent = new PaymentIntent();
        intent.setBooking(bookingOpt.get());
        intent.setAmount(amount);
        intent.setStatus(PaymentIntent.Status.AUTHORIZED);
        String now = now().toString();
        intent.setCreatedAt(now);
        intent.setUpdatedAt(now);
        intent.setIdempotencyKey(idempotencyKey);
        paymentIntentRepository.save(intent);

        return ResponseEntity.ok(intent);
    }

    // Capture payment
    @PostMapping("/capture")
    public ResponseEntity<PaymentIntent> capturePayment(@RequestParam Long paymentIntentId) {
        Optional<PaymentIntent> intentOpt = paymentIntentRepository.findById(paymentIntentId);
        if (intentOpt.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        PaymentIntent intent = intentOpt.get();
        if (intent.getStatus() != PaymentIntent.Status.AUTHORIZED) {
            return ResponseEntity.status(409).build();
        }
        intent.setStatus(PaymentIntent.Status.CAPTURED);
        intent.setUpdatedAt(now().toString());
        paymentIntentRepository.save(intent);
        return ResponseEntity.ok(intent);
    }
}
