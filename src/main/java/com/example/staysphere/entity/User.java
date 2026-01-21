package com.example.staysphere.entity;

import jakarta.persistence.*;
import lombok.*;

import java.util.List;

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

    @jakarta.persistence.Convert(converter = com.example.staysphere.security.AesGcmEncryptor.class)
    private String name;

    @Column(unique = true, nullable = false)
    @jakarta.persistence.Convert(converter = com.example.staysphere.security.AesGcmEncryptor.class)
    private String email;

    private String password;

    @Enumerated(EnumType.STRING)
    private Role role;

    // A user can own multiple properties
    @OneToMany(mappedBy = "owner", cascade = CascadeType.ALL)
    private List<Property> properties;

    public enum Role {
        GUEST,
        PROPERTY_MANAGER,
        ADMIN,
        FRONT_DESK
    }
}
