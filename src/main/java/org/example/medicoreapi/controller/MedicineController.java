package org.example.medicoreapi.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.example.medicoreapi.dto.request.MedicineRequest;
import org.example.medicoreapi.dto.response.ApiResponse;
import org.example.medicoreapi.dto.response.MedicineResponse;
import org.example.medicoreapi.service.MedicineService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * ===================================================================
 * CONTROLLER: MedicineController (API Thuốc)
 * NGƯỜI LÀM: Người 5 - Trần Đăng Việt (Medicine + Prescription)
 * ===================================================================
 */
@RestController
@RequestMapping("/api/medicines")
@RequiredArgsConstructor
public class MedicineController {

    private final MedicineService medicineService;

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<MedicineResponse>> createMedicine(@Valid @RequestBody MedicineRequest request) {
        MedicineResponse data = medicineService.createMedicine(request);
        return ResponseEntity.ok(ApiResponse.<MedicineResponse>builder()
                .success(true)
                .message("Tạo thuốc mới thành công")
                .data(data)
                .build());
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'DOCTOR')")
    public ResponseEntity<ApiResponse<List<MedicineResponse>>> getAllMedicines() {
        List<MedicineResponse> data = medicineService.getAllMedicines();
        return ResponseEntity.ok(ApiResponse.<List<MedicineResponse>>builder()
                .success(true)
                .message("Lấy danh sách thuốc thành công")
                .data(data)
                .build());
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'DOCTOR', 'PATIENT')")
    public ResponseEntity<ApiResponse<MedicineResponse>> getMedicineById(@PathVariable Long id) {
        MedicineResponse data = medicineService.getMedicineById(id);
        return ResponseEntity.ok(ApiResponse.<MedicineResponse>builder()
                .success(true)
                .message("Lấy chi tiết thuốc thành công")
                .data(data)
                .build());
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<MedicineResponse>> updateMedicine(
            @PathVariable Long id,
            @Valid @RequestBody MedicineRequest request) {
        MedicineResponse data = medicineService.updateMedicine(id, request);
        return ResponseEntity.ok(ApiResponse.<MedicineResponse>builder()
                .success(true)
                .message("Cập nhật thông tin thuốc thành công")
                .data(data)
                .build());
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<Void>> deleteMedicine(@PathVariable Long id) {
        medicineService.deleteMedicine(id);
        return ResponseEntity.ok(ApiResponse.<Void>builder()
                .success(true)
                .message("Xóa thuốc thành công")
                .build());
    }
}
