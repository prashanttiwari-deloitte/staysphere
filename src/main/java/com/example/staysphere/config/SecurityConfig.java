package com.example.staysphere.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
public class SecurityConfig {

    @Bean
    public UserDetailsService userDetailsService(PasswordEncoder passwordEncoder) {
        InMemoryUserDetailsManager manager = new InMemoryUserDetailsManager();
        manager.createUser(User.withUsername("guest")
                .password(passwordEncoder.encode("guestpass"))
                .roles("GUEST")
                .build());
        manager.createUser(User.withUsername("manager")
                .password(passwordEncoder.encode("managerpass"))
                .roles("PROPERTY_MANAGER")
                .build());
        manager.createUser(User.withUsername("admin")
                .password(passwordEncoder.encode("adminpass"))
                .roles("ADMIN")
                .build());
        return manager;
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
            .csrf(csrf -> csrf.disable())
            .authorizeHttpRequests(auth -> auth
                .requestMatchers("/v3/api-docs/**", "/swagger-ui/**", "/swagger-ui.html").permitAll()
                .requestMatchers("/api/users/**").hasAnyRole("ADMIN", "PROPERTY_MANAGER")
                .requestMatchers("/api/properties/**").hasAnyRole("ADMIN", "PROPERTY_MANAGER")
                .requestMatchers("/api/rooms/**").hasAnyRole("ADMIN", "PROPERTY_MANAGER")
                .anyRequest().authenticated()
            )
            .httpBasic(org.springframework.security.config.Customizer.withDefaults());
        return http.build();
    }
}
