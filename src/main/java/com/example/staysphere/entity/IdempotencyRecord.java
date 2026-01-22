package com.example.staysphere.entity;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Data
public class IdempotencyRecord {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true)
    private String idempotencyKey;

    private String requestHash;

    @Lob
    private String responseBody;

    private String createdAt;
}
