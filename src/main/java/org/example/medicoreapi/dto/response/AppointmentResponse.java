package org.example.medicoreapi.dto.response;

import lombok.Data;

import java.time.LocalDate;

@Data
public class AppointmentResponse {
	private Long id;
	private LocalDate appointmentDate;
	private String timeSlot;
	private String status;
	private String notes;
	private String patientName;
	private Long doctorId;
	private String doctorName;
}

