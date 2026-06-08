package org.example.medicoreapi.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class MedicineRequest {
    @NotBlank
    private String name;

    private String unit;
    private String description;

    @PositiveOrZero
    private BigDecimal price;
}
