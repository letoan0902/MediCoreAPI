package org.example.medicoreapi.dto.response;

import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
public class PrescriptionResponse {
    private Long id;
    private Long appointmentId;
    private Long doctorId;
    private String doctorName;
    private Long patientId;
    private String patientName;
    private String diagnosis;
    private String notes;
    private LocalDateTime createdAt;
    private List<PrescriptionItemResponse> items;

    @Data
    public static class PrescriptionItemResponse {
        private Long medicineId;
        private String medicineName;
        private String unit;
        private Integer quantity;
        private String dosage;
        private String notes;
    }
}
