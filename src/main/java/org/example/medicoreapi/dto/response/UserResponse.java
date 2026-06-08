package org.example.medicoreapi.dto.response;

import lombok.Data;
import org.example.medicoreapi.enums.Role;

import java.time.LocalDateTime;

@Data
public class UserResponse {
    private Long id;
    private String username;
    private Role role;
    private boolean enabled;
    private Long doctorId;
    private Long patientId;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
