package com.example.staysphere.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.staysphere.entity.Property;

public interface PropertyRepository extends JpaRepository<Property, Long> {
    // Additional query methods can be defined here if needed
}
