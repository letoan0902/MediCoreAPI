package org.example.medicoreapi.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

import java.time.LocalDate;

@Data
public class PatientRequest {
    @NotBlank
    private String fullName;

    private LocalDate dateOfBirth;
    private String gender;
    private String phone;
    private String address;
    private String username;
}
