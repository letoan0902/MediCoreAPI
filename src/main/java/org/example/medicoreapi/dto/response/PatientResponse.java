package org.example.medicoreapi.dto.response;

import lombok.*;

import java.time.LocalDate;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PatientResponse {
    private Long id;

    private String fullName;

    private LocalDate dateOfBirth;

    private String gender;

    private String phone;

    private String address;
}