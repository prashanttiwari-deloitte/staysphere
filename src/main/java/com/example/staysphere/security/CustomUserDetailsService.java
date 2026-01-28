package com.example.staysphere.security;

import com.example.staysphere.entity.User;
import com.example.staysphere.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;


@Service
public class CustomUserDetailsService implements UserDetailsService {

    @Autowired
    private UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
       return userRepository.findAll().stream()
            .filter(u -> u.getEmail().equals(email))
            .findFirst()
            .orElseThrow(() -> new UsernameNotFoundException("User not found with email: " + email));   
    }
}
