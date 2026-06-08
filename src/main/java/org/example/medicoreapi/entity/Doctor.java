package org.example.medicoreapi.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "doctors")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Doctor {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(nullable = false)
	private String fullName;

	private String specialization;

	private String phone;

	private String email;

	// For simplicity store the username of the linked User (authentication principal)
	// This avoids tight coupling to a User entity that may be implemented elsewhere
	@Column(unique = true)
	private String username;

	private LocalDateTime createdAt;

	private LocalDateTime updatedAt;

	@OneToMany(mappedBy = "doctor", cascade = CascadeType.ALL)
	private List<Appointment> appointments;
}

