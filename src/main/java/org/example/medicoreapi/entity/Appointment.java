package org.example.medicoreapi.entity;

import jakarta.persistence.*;
import lombok.*;
import org.example.medicoreapi.enums.AppointmentStatus;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "appointments")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Appointment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private LocalDate appointmentDate;

    private String timeSlot;

    @Enumerated(EnumType.STRING)
    private AppointmentStatus status;

    private String notes;

    private LocalDateTime createdAt;

    @ManyToOne
    @JoinColumn(name = "doctor_id")
    private Doctor doctor;

    @ManyToOne
    @JoinColumn(name = "patient_id")
    private Patient patient;

    @OneToOne(mappedBy = "appointment")
    private Prescription prescription;

    @PrePersist
    public void onCreate() {
        createdAt = LocalDateTime.now();
    }
}