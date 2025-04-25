package com.avolta.dto;

import com.avolta.models.User;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserDto {
    private String id;
    private String email;
    private User.Role role;
    private LocalDateTime createdAt;
    private LocalDateTime lastLogin;
    private User.Status status;

    public static UserDto fromEntity(User user) {
        UserDto dto = new UserDto();
        dto.setId(user.getId());
        dto.setEmail(user.getEmail());
        dto.setRole(user.getRole());
        dto.setCreatedAt(user.getCreatedAt());
        dto.setLastLogin(user.getLastLogin());
        dto.setStatus(user.getStatus());
        return dto;
    }
}