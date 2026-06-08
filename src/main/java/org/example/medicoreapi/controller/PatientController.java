package org.example.medicoreapi.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.example.medicoreapi.dto.request.AppointmentRequest;
import org.example.medicoreapi.dto.request.PatientRequest;
import org.example.medicoreapi.dto.response.ApiResponse;
import org.example.medicoreapi.dto.response.AppointmentResponse;
import org.example.medicoreapi.dto.response.PatientResponse;
import org.example.medicoreapi.service.PatientService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/patients")
@RequiredArgsConstructor
public class PatientController {

    private final PatientService patientService;

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<PatientResponse>> createPatient(
            @Valid @RequestBody PatientRequest request
    ) {
        PatientResponse response = patientService.createPatient(request);

        return ResponseEntity.status(HttpStatus.CREATED).body(
                ApiResponse.<PatientResponse>builder()
                        .success(true)
                        .message("Create patient successfully")
                        .data(response)
                        .build()
        );
    }

    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<List<PatientResponse>>> getAllPatients() {
        List<PatientResponse> response = patientService.getAllPatients();

        return ResponseEntity.ok(
                ApiResponse.<List<PatientResponse>>builder()
                        .success(true)
                        .message("Get all patients successfully")
                        .data(response)
                        .build()
        );
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<PatientResponse>> getPatientById(
            @PathVariable Long id
    ) {
        PatientResponse response = patientService.getPatientById(id);

        return ResponseEntity.ok(
                ApiResponse.<PatientResponse>builder()
                        .success(true)
                        .message("Get patient successfully")
                        .data(response)
                        .build()
        );
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<PatientResponse>> updatePatient(
            @PathVariable Long id,
            @Valid @RequestBody PatientRequest request
    ) {
        PatientResponse response = patientService.updatePatient(id, request);

        return ResponseEntity.ok(
                ApiResponse.<PatientResponse>builder()
                        .success(true)
                        .message("Update patient successfully")
                        .data(response)
                        .build()
        );
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<Void>> deletePatient(
            @PathVariable Long id
    ) {
        patientService.deletePatient(id);

        return ResponseEntity.ok(
                ApiResponse.<Void>builder()
                        .success(true)
                        .message("Delete patient successfully")
                        .data(null)
                        .build()
        );
    }

    @GetMapping("/my-profile")
    @PreAuthorize("hasRole('PATIENT')")
    public ResponseEntity<ApiResponse<PatientResponse>> getMyProfile(
            Authentication authentication
    ) {
        Long userId = getCurrentUserId(authentication);
        PatientResponse response = patientService.getMyProfile(userId);

        return ResponseEntity.ok(
                ApiResponse.<PatientResponse>builder()
                        .success(true)
                        .message("Get my profile successfully")
                        .data(response)
                        .build()
        );
    }

    @PostMapping("/appointments")
    @PreAuthorize("hasRole('PATIENT')")
    public ResponseEntity<ApiResponse<AppointmentResponse>> bookAppointment(
            Authentication authentication,
            @Valid @RequestBody AppointmentRequest request
    ) {
        Long userId = getCurrentUserId(authentication);
        AppointmentResponse response = patientService.bookAppointment(userId, request);

        return ResponseEntity.status(HttpStatus.CREATED).body(
                ApiResponse.<AppointmentResponse>builder()
                        .success(true)
                        .message("Book appointment successfully")
                        .data(response)
                        .build()
        );
    }

    @GetMapping("/my-appointments")
    @PreAuthorize("hasRole('PATIENT')")
    public ResponseEntity<ApiResponse<List<AppointmentResponse>>> getMyAppointments(
            Authentication authentication
    ) {
        Long userId = getCurrentUserId(authentication);
        List<AppointmentResponse> response = patientService.getMyAppointments(userId);

        return ResponseEntity.ok(
                ApiResponse.<List<AppointmentResponse>>builder()
                        .success(true)
                        .message("Get my appointments successfully")
                        .data(response)
                        .build()
        );
    }

    private Long getCurrentUserId(Authentication authentication) {
        return Long.parseLong(authentication.getName());
    }
}