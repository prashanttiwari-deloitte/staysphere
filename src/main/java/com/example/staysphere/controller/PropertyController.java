package com.example.staysphere.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.example.staysphere.entity.Property;
import com.example.staysphere.repository.PropertyRepository;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/properties")
public class PropertyController {

    @Autowired
    private PropertyRepository propertyRepository;

    @GetMapping
    public List<Property> getAllProperties() {
        return propertyRepository.findAll();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Property> getPropertyById(@PathVariable Long id) {
        Optional<Property> property = propertyRepository.findById(id);
        return property.map(ResponseEntity::ok)
                       .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public Property createProperty(@RequestBody Property property) {
        return propertyRepository.save(property);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Property> updateProperty(@PathVariable Long id, @RequestBody Property propertyDetails) {
        return propertyRepository.findById(id)
            .map(property -> {
                property.setName(propertyDetails.getName());
                property.setAddress(propertyDetails.getAddress());
                property.setOwner(propertyDetails.getOwner());
                return ResponseEntity.ok(propertyRepository.save(property));
            })
            .orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteProperty(@PathVariable Long id) {
        return propertyRepository.findById(id)
            .map(property -> {
                propertyRepository.delete(property);
                return ResponseEntity.noContent().<Void>build();
            })
            .orElse(ResponseEntity.notFound().build());
    }
}
