package com.example.staysphere.dto;

import com.example.staysphere.entity.User;
import com.example.staysphere.util.PiiMaskingUtil;
import lombok.Data;

@Data
public class UserResponse {
    private Long id;
    private String name;
    private String email;
    private String role;

    public UserResponse(User user) {
        this.id = user.getId();
        this.name = PiiMaskingUtil.maskName(user.getName());
        this.email = PiiMaskingUtil.maskEmail(user.getEmail());
        this.role = user.getRole() != null ? user.getRole().name() : null;
    }
}
