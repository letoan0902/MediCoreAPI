package org.example.medicoreapi.dto.response;

import lombok.*;
import org.example.medicoreapi.enums.AppointmentStatus;

import java.time.LocalDate;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AppointmentResponse {
    private Long id;

    private String doctorName;

    private String patientName;

    private LocalDate appointmentDate;

    private String timeSlot;

    private AppointmentStatus status;

    private String notes;
}