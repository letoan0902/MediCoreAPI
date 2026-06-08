package org.example.medicoreapi.controller;

import jakarta.validation.Valid;
import org.example.medicoreapi.dto.request.PrescriptionRequest;
import org.example.medicoreapi.dto.response.ApiResponse;
import org.example.medicoreapi.dto.response.PrescriptionResponse;
import org.example.medicoreapi.service.PrescriptionService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.security.Principal;
import java.util.List;

@RestController
@RequestMapping("/api/prescriptions")
public class PrescriptionController {
    private final PrescriptionService prescriptionService;

    public PrescriptionController(PrescriptionService prescriptionService) {
        this.prescriptionService = prescriptionService;
    }

    @PreAuthorize("hasRole('DOCTOR')")
    @PostMapping
    public ResponseEntity<ApiResponse<PrescriptionResponse>> createPrescription(
            Principal principal,
            @Valid @RequestBody PrescriptionRequest request
    ) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("Prescription created", prescriptionService.createPrescription(principal.getName(), request)));
    }

    @PreAuthorize("hasAnyRole('ADMIN','DOCTOR','PATIENT')")
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<PrescriptionResponse>> getPrescription(@PathVariable Long id, Authentication authentication) {
        return ResponseEntity.ok(ApiResponse.success("Prescription retrieved", prescriptionService.getPrescriptionById(id, authentication)));
    }

    @PreAuthorize("hasRole('PATIENT')")
    @GetMapping("/my-prescriptions")
    public ResponseEntity<ApiResponse<List<PrescriptionResponse>>> myPrescriptions(Principal principal) {
        return ResponseEntity.ok(ApiResponse.success("Prescriptions retrieved", prescriptionService.getPatientPrescriptions(principal.getName())));
    }
}
