package org.example.medicoreapi.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;


@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PrescriptionRequest {

    @NotNull(message = "ID cuộc hẹn không được để trống")
    private Long appointmentId;

    @NotBlank(message = "Chuẩn đoán không được để trống")
    private String diagnosis;

    private String notes;

    @NotEmpty(message = "Đơn thuốc phải có ít nhất một loại thuốc")
    private List<PrescriptionItemRequest> items;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class PrescriptionItemRequest {
        @NotNull(message = "ID thuốc không được để trống")
        private Long medicineId;

        @NotNull(message = "Số lượng không được để trống")
        private Integer quantity;

        @NotBlank(message = "Liều dùng không được để trống")
        private String dosage;

        private String notes;
    }
}
