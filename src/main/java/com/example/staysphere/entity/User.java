package com.example.staysphere.entity;

import jakarta.persistence.*;
import lombok.*;

import java.util.List;

import com.example.staysphere.security.AesGcmEncryptor;

@Entity
@Table(name = "users")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Convert(converter = AesGcmEncryptor.class)
    private String name;

    @Column(unique = true, nullable = false)
    @Convert(converter = AesGcmEncryptor.class)
    private String email;

    private String password;

    @Enumerated(EnumType.STRING)
    private Role role;

    // A user can own multiple properties
    @OneToMany(mappedBy = "owner", cascade = CascadeType.ALL)
    private List<Property> properties;

    @OneToMany(mappedBy = "guest", cascade = CascadeType.ALL)
    private List<Booking> bookings;

    public enum Role {
        GUEST,
        PROPERTY_MANAGER,
        ADMIN,
        FRONT_DESK
    }
}
