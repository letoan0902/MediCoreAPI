package org.example.medicoreapi.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class DoctorRequest {
	@NotBlank
	private String fullName;

	private String specialization;

	private String phone;

	@Email
	private String email;

	// Optional: link to existing user by username
	private String username;
}

