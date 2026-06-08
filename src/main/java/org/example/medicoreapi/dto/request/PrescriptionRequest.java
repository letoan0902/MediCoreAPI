package org.example.medicoreapi.dto.request;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.List;

@Data
public class PrescriptionRequest {
    @NotNull
    private Long appointmentId;

    @NotBlank
    private String diagnosis;

    private String notes;

    @Valid
    @NotEmpty
    private List<PrescriptionItemRequest> items;

    @Data
    public static class PrescriptionItemRequest {
        @NotNull
        private Long medicineId;

        @NotNull
        @Min(1)
        private Integer quantity;

        @NotBlank
        private String dosage;

        private String notes;
    }
}
