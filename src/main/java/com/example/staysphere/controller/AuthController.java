package com.example.staysphere.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import com.example.staysphere.dto.RegistrationRequest;
import com.example.staysphere.repository.UserRepository;
import com.example.staysphere.entity.User;
import java.util.Optional;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.http.ResponseEntity;
import com.example.staysphere.security.CustomUserDetailsService;


import com.example.staysphere.dto.LoginRequest;
import com.example.staysphere.security.JwtHelper;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;


@RestController
@RequestMapping("/api/auth")
public class AuthController {
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private PasswordEncoder passwordEncoder;
     @Autowired
     private JwtHelper jwtHelper;
     @Autowired
     private CustomUserDetailsService userDetailsService;
    @Autowired
    private AuthenticationManager authenticationManager;

    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody RegistrationRequest registrationRequest) {
        try{
            String email = registrationRequest.getEmail();
            Optional<User> existing = userRepository.findByEmail(email);
        if (existing.isPresent()) {
            return ResponseEntity.badRequest().body("Email already in use");
        }
        String roleStr = registrationRequest.getRole();
        User.Role role = User.Role.GUEST;

        if(roleStr != null && !roleStr.isEmpty()){
            try{
                role = User.Role.valueOf(roleStr.toUpperCase());
            }catch(IllegalArgumentException e){
                return ResponseEntity.badRequest().body("Invalid role specified");
            }
        }

        User user = User.builder()
                .name(registrationRequest.getName())
                .email(registrationRequest.getEmail())
                .password(passwordEncoder.encode(registrationRequest.getPassword()))
                .role(role)
                .build();
        userRepository.save(user);
        return ResponseEntity.ok("Registration successful");
        }catch(Exception e){
            return ResponseEntity.status(500).body("An error occurred during registration");
        }
    }



  @PostMapping("/login")
    public ResponseEntity<String> login(@RequestBody LoginRequest loginRequest) {
       try{
        UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(
            loginRequest.getEmail(),
            loginRequest.getPassword()
        );
        authenticationManager.authenticate(authenticationToken);
       }catch(Exception e){
        return ResponseEntity.status(401).body("Invalid Credentials");
       }
        final UserDetails userDetails =
            userDetailsService.loadUserByUsername(loginRequest.getEmail());
        final String token = jwtHelper.generateToken(userDetails);
        return ResponseEntity.ok(token);

    }
}
