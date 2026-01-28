package com.example.staysphere.repository;

import com.example.staysphere.entity.PaymentIntent;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface PaymentIntentRepository extends JpaRepository<PaymentIntent, Long> {
    Optional<PaymentIntent> findByIdempotencyKey(String idempotencyKey);
}
