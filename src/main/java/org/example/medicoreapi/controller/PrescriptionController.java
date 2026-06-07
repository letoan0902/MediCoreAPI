package org.example.medicoreapi.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.example.medicoreapi.dto.request.PrescriptionRequest;
import org.example.medicoreapi.dto.response.ApiResponse;
import org.example.medicoreapi.dto.response.PrescriptionResponse;
import org.example.medicoreapi.service.PrescriptionService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * ===================================================================
 * CONTROLLER: PrescriptionController (API Đơn thuốc)
 * NGƯỜI LÀM: Người 5 - Trần Đăng Việt (Medicine + Prescription)
 * ===================================================================
 */
@RestController
@RequestMapping("/api/prescriptions")
@RequiredArgsConstructor
public class PrescriptionController {

    private final PrescriptionService prescriptionService;

    @PostMapping
    @PreAuthorize("hasRole('DOCTOR')")
    public ResponseEntity<ApiResponse<PrescriptionResponse>> createPrescription(
            @Valid @RequestBody PrescriptionRequest request,
            Authentication authentication) {
        
        // Giả sử Principal là User entity hoặc có id bác sĩ
        // Ở đây tạm lấy ID từ logic của Người 2/3 (Ví dụ User.id hoặc Doctor.id)
        // Trong thực tế sẽ lấy từ CustomUserDetails
        Long doctorId = 1L; // TODO: Lấy ID từ authentication principal

        PrescriptionResponse data = prescriptionService.createPrescription(doctorId, request);
        return ResponseEntity.status(201).body(ApiResponse.<PrescriptionResponse>builder()
                .success(true)
                .message("Tạo đơn thuốc thành công")
                .data(data)
                .build());
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('DOCTOR', 'PATIENT', 'ADMIN')")
    public ResponseEntity<ApiResponse<PrescriptionResponse>> getPrescriptionById(@PathVariable Long id) {
        PrescriptionResponse data = prescriptionService.getPrescriptionById(id);
        return ResponseEntity.ok(ApiResponse.<PrescriptionResponse>builder()
                .success(true)
                .message("Lấy thông tin đơn thuốc thành công")
                .data(data)
                .build());
    }

    @GetMapping("/patient/{patientId}")
    @PreAuthorize("hasAnyRole('ADMIN', 'DOCTOR')")
    public ResponseEntity<ApiResponse<List<PrescriptionResponse>>> getPrescriptionsByPatient(@PathVariable Long patientId) {
        List<PrescriptionResponse> data = prescriptionService.getPatientPrescriptions(patientId);
        return ResponseEntity.ok(ApiResponse.<List<PrescriptionResponse>>builder()
                .success(true)
                .message("Lấy lịch sử đơn thuốc bệnh nhân thành công")
                .data(data)
                .build());
    }

    @GetMapping("/my-prescriptions")
    @PreAuthorize("hasRole('PATIENT')")
    public ResponseEntity<ApiResponse<List<PrescriptionResponse>>> getMyPrescriptions(Authentication authentication) {
        Long patientId = 1L; // TODO: Lấy patientId từ authentication principal
        List<PrescriptionResponse> data = prescriptionService.getPatientPrescriptions(patientId);
        return ResponseEntity.ok(ApiResponse.<List<PrescriptionResponse>>builder()
                .success(true)
                .message("Lấy lịch sử đơn thuốc của bạn thành công")
                .data(data)
                .build());
    }
}
