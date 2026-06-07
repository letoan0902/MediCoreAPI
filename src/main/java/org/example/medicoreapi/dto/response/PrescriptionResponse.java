package org.example.medicoreapi.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PrescriptionResponse {
    private Long id;
    private String doctorName;
    private String patientName;
    private String diagnosis;
    private String notes;
    private LocalDateTime createdAt;
    private List<PrescriptionItemResponse> items;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class PrescriptionItemResponse {
        private String medicineName;
        private Integer quantity;
        private String dosage;
        private String notes;
    }
}
