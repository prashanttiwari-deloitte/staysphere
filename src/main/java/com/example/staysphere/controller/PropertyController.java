package com.example.staysphere.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;

import com.example.staysphere.entity.Property;
import com.example.staysphere.repository.PropertyRepository;
import com.example.staysphere.repository.UserRepository;

import java.util.List;
import java.util.Optional;
import org.springframework.security.core.context.SecurityContextHolder;
import com.example.staysphere.dto.PropertyRequest;
import com.example.staysphere.entity.User;

@RestController
@RequestMapping("/api/properties")
public class PropertyController {

    @Autowired
    private PropertyRepository propertyRepository;

    @Autowired 
    private UserRepository userRepository;

    @GetMapping("getAll")
    //@PreAuthorize("hasRole('ADMIN') or hasRole('PROPERTY_MANAGER')")
    public List<Property> getAllProperties() {
        return propertyRepository.findAll();
    }

    @GetMapping("get/{id}")
    //@PreAuthorize("hasRole('ADMIN') or hasRole('PROPERTY_MANAGER')")
    public ResponseEntity<Property> getPropertyById(@PathVariable Long id) {
        Optional<Property> property = propertyRepository.findById(id);
        return property.map(ResponseEntity::ok)
                       .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping("/create")
    @PreAuthorize("hasRole('ADMIN') or hasRole('PROPERTY_MANAGER')")
    public ResponseEntity<Property> createProperty(@RequestBody PropertyRequest propertyRequest) {
        try{
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            if (auth == null || !auth.isAuthenticated()) {  
                throw new RuntimeException("Unauthorized");
            }  
            Optional<User> userOpt = userRepository.findByEmail(auth.getName()); 
            
            Property property = new Property();
            property.setName(propertyRequest.getName());
            property.setAddress(propertyRequest.getAddress());
            property.setRooms(propertyRequest.getRooms());
            property.setOwner(userOpt.get());
            Property result = propertyRepository.save(property);
            return ResponseEntity.ok(result);
        }catch(Exception e){
            return ResponseEntity.status(500).build();
        }
    }

    @PutMapping("update/{id}")
    @PreAuthorize("hasRole('ADMIN') or hasRole('PROPERTY_MANAGER')")
    public ResponseEntity<Property> updateProperty(@PathVariable Long id, @RequestBody PropertyRequest propertyDetails) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !auth.isAuthenticated()) {
            return ResponseEntity.status(403).build();
        }

        boolean isAdmin = auth.getAuthorities()
                .stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));
        boolean isManager = auth.getAuthorities()
        .stream()
        .anyMatch(x -> x.getAuthority().equals("ROLE_PROPERTY_MANAGER"));

        String userEmail = auth.getName();

        Optional<Property> propertyOpt = propertyRepository.findById(id);
        if (propertyOpt.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        Property property = propertyOpt.get();
        if (isAdmin) {
            property.setName(propertyDetails.getName());
            property.setAddress(propertyDetails.getAddress());
            return ResponseEntity.ok(propertyRepository.save(property));
        } else if (isManager) {
            if (property.getOwner() != null && property.getOwner().getEmail().equals(userEmail)) {
                property.setName(propertyDetails.getName());
                property.setAddress(propertyDetails.getAddress());
                return ResponseEntity.ok(propertyRepository.save(property));
            } else {
                return ResponseEntity.status(403).build();
            }
        } else {
            return ResponseEntity.status(403).build();
        }
    }

    @DeleteMapping("delete/{id}")
    @PreAuthorize("hasRole('ADMIN') or hasRole('PROPERTY_MANAGER')")
    public ResponseEntity<Void> deleteProperty(@PathVariable Long id) {
        try {
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            if (auth == null || !auth.isAuthenticated()) {
                return ResponseEntity.status(403).build();
            }

            
            boolean isAdmin = auth.getAuthorities()
                    .stream()
                    .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));

           
            boolean isManager = auth.getAuthorities()
                    .stream()
                    .anyMatch(a -> a.getAuthority().equals("ROLE_PROPERTY_MANAGER"));

           
            String userEmail = auth.getName();

            Optional<Property> propertyOpt = propertyRepository.findById(id);
            if (propertyOpt.isEmpty()) {
                return ResponseEntity.notFound().build();
            }
            Property property = propertyOpt.get();
            if (isAdmin) {
                propertyRepository.delete(property);
                return ResponseEntity.noContent().build();
            } else if (isManager) {
                if (property.getOwner() != null && property.getOwner().getEmail().equals(userEmail)) {
                    propertyRepository.delete(property);
                    return ResponseEntity.noContent().build();
                } else {
                    return ResponseEntity.status(403).build();
                }
            } else {
                return ResponseEntity.status(403).build();
            }
        } catch (Exception e) {
            return ResponseEntity.status(500).build();
        }
    }
}
