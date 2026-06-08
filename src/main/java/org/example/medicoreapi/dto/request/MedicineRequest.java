package org.example.medicoreapi.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MedicineRequest {

    @NotBlank(message = "Tên thuốc không được để trống")
    private String name;

    private String unit;

    private String description;

    @PositiveOrZero(message = "Giá thuốc không được âm")
    private BigDecimal price;
}
