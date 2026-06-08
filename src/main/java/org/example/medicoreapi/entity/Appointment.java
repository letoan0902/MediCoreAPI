package org.example.medicoreapi.entity;

import jakarta.persistence.*;
import lombok.*;
import org.example.medicoreapi.enums.AppointmentStatus;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "appointments")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Appointment {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	private LocalDate appointmentDate;

	private String timeSlot;

	@Enumerated(EnumType.STRING)
	private AppointmentStatus status;

	@Column(length = 1000)
	private String notes;

	// For simplicity we store patient name directly; patient entity may be linked later
	private String patientName;

	private LocalDateTime createdAt;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "doctor_id")
	private Doctor doctor;
}

