package org.example.medicoreapi.controller;

import jakarta.validation.Valid;
import org.example.medicoreapi.dto.request.DoctorRequest;
import org.example.medicoreapi.dto.response.AppointmentResponse;
import org.example.medicoreapi.dto.response.DoctorResponse;
import org.example.medicoreapi.service.DoctorService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;

@RestController
@RequestMapping("/api/doctors")
public class DoctorController {
	private final DoctorService doctorService;

	public DoctorController(DoctorService doctorService) {
		this.doctorService = doctorService;
	}

	// ADMIN - create doctor
	@PreAuthorize("hasRole('ADMIN')")
	@PostMapping
	public ResponseEntity<DoctorResponse> createDoctor(@Valid @RequestBody DoctorRequest request) {
		DoctorResponse created = doctorService.createDoctor(request);
		return ResponseEntity.status(HttpStatus.CREATED).body(created);
	}

	@PreAuthorize("hasRole('ADMIN')")
	@GetMapping
	public List<DoctorResponse> listDoctors() {
		return doctorService.getAllDoctors();
	}

	@PreAuthorize("hasRole('ADMIN')")
	@GetMapping("/{id}")
	public DoctorResponse getDoctor(@PathVariable Long id) {
		return doctorService.getDoctorById(id);
	}

	@PreAuthorize("hasRole('ADMIN')")
	@PutMapping("/{id}")
	public DoctorResponse updateDoctor(@PathVariable Long id, @Valid @RequestBody DoctorRequest request) {
		return doctorService.updateDoctor(id, request);
	}

	@PreAuthorize("hasRole('ADMIN')")
	@DeleteMapping("/{id}")
	public ResponseEntity<Void> deleteDoctor(@PathVariable Long id) {
		doctorService.deleteDoctor(id);
		return ResponseEntity.noContent().build();
	}

	// DOCTOR - view own appointments
	@PreAuthorize("hasRole('DOCTOR')")
	@GetMapping("/my-appointments")
	public List<AppointmentResponse> myAppointments(Principal principal) {
		return doctorService.getMyAppointments(principal);
	}

	@PreAuthorize("hasRole('DOCTOR')")
	@GetMapping("/my-appointments/today")
	public List<AppointmentResponse> myTodayAppointments(Principal principal) {
		return doctorService.getMyTodayAppointments(principal);
	}
}

