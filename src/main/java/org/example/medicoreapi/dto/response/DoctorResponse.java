package org.example.medicoreapi.dto.response;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class DoctorResponse {
	private Long id;
	private String fullName;
	private String specialization;
	private String phone;
	private String email;
	private String username;
	private LocalDateTime createdAt;
	private LocalDateTime updatedAt;
}

