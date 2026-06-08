package org.example.medicoreapi.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;
import org.example.medicoreapi.enums.Role;

import java.time.LocalDate;

@Data
public class RegisterRequest {
    @NotBlank
    private String username;

    @NotBlank
    @Size(min = 6)
    private String password;

    private Role role;
    private Boolean enabled;

    private String fullName;
    private String phone;

    @Email
    private String email;

    private String specialization;
    private LocalDate dateOfBirth;
    private String gender;
    private String address;
}
