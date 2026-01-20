package com.example.staysphere.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.staysphere.entity.User;

public interface UserRepository extends JpaRepository<User, Long> {
    // Additional query methods can be defined here if needed
}
