package com.example.staysphere.entity;

import jakarta.persistence.*;
import lombok.*;

import java.util.List;
import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;

@Entity
@Table(name = "properties")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Property {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    private String address;

    // Many properties can be owned by one user
    @ManyToOne
    @JoinColumn(name = "owner_id")
    @JsonBackReference
    private User owner;

    // A property can have multiple rooms
    @OneToMany(mappedBy = "property", cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    @JsonManagedReference
    private List<Room> rooms;
}
