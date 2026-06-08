package org.example.medicoreapi.entity;

import jakarta.persistence.*;
import lombok.*;
import org.example.medicoreapi.enums.AppointmentStatus;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "appointments")
@Getter
@Setter
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

	private LocalDateTime createdAt;

	private LocalDateTime updatedAt;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "doctor_id")
	@ToString.Exclude
	private Doctor doctor;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "patient_id")
	@ToString.Exclude
	private Patient patient;

	@OneToOne(mappedBy = "appointment")
	@ToString.Exclude
	private Prescription prescription;

	@PrePersist
	void onCreate() {
		LocalDateTime now = LocalDateTime.now();
		createdAt = now;
		updatedAt = now;
		if (status == null) {
			status = AppointmentStatus.PENDING;
		}
	}

	@PreUpdate
	void onUpdate() {
		updatedAt = LocalDateTime.now();
	}
}

