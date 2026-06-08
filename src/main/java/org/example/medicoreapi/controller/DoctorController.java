package org.example.medicoreapi.controller;

import jakarta.validation.Valid;
import org.example.medicoreapi.dto.request.DoctorRequest;
import org.example.medicoreapi.dto.response.ApiResponse;
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
	public ResponseEntity<ApiResponse<DoctorResponse>> createDoctor(@Valid @RequestBody DoctorRequest request) {
		DoctorResponse created = doctorService.createDoctor(request);
		return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.success("Doctor created", created));
	}

	@PreAuthorize("hasAnyRole('ADMIN','PATIENT')")
	@GetMapping
	public ResponseEntity<ApiResponse<List<DoctorResponse>>> listDoctors() {
		return ResponseEntity.ok(ApiResponse.success("Doctors retrieved", doctorService.getAllDoctors()));
	}

	@PreAuthorize("hasAnyRole('ADMIN','PATIENT')")
	@GetMapping("/{id}")
	public ResponseEntity<ApiResponse<DoctorResponse>> getDoctor(@PathVariable Long id) {
		return ResponseEntity.ok(ApiResponse.success("Doctor retrieved", doctorService.getDoctorById(id)));
	}

	@PreAuthorize("hasRole('ADMIN')")
	@PutMapping("/{id}")
	public ResponseEntity<ApiResponse<DoctorResponse>> updateDoctor(@PathVariable Long id, @Valid @RequestBody DoctorRequest request) {
		return ResponseEntity.ok(ApiResponse.success("Doctor updated", doctorService.updateDoctor(id, request)));
	}

	@PreAuthorize("hasRole('ADMIN')")
	@DeleteMapping("/{id}")
	public ResponseEntity<ApiResponse<Void>> deleteDoctor(@PathVariable Long id) {
		doctorService.deleteDoctor(id);
		return ResponseEntity.status(HttpStatus.NO_CONTENT).body(ApiResponse.success("Doctor deleted", null));
	}

	// DOCTOR - view own appointments
	@PreAuthorize("hasRole('DOCTOR')")
	@GetMapping("/my-appointments")
	public ResponseEntity<ApiResponse<List<AppointmentResponse>>> myAppointments(Principal principal) {
		return ResponseEntity.ok(ApiResponse.success("Appointments retrieved", doctorService.getMyAppointments(principal)));
	}

	@PreAuthorize("hasRole('DOCTOR')")
	@GetMapping("/my-appointments/today")
	public ResponseEntity<ApiResponse<List<AppointmentResponse>>> myTodayAppointments(Principal principal) {
		return ResponseEntity.ok(ApiResponse.success("Today appointments retrieved", doctorService.getMyTodayAppointments(principal)));
	}
}

