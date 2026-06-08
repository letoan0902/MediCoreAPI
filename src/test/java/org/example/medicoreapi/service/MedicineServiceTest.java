package org.example.medicoreapi.service;

import org.example.medicoreapi.dto.request.MedicineRequest;
import org.example.medicoreapi.dto.response.MedicineResponse;
import org.example.medicoreapi.entity.Medicine;
import org.example.medicoreapi.repository.MedicineRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class MedicineServiceTest {
    private MedicineRepository medicineRepository;
    private MedicineService medicineService;

    @BeforeEach
    void setUp() {
        medicineRepository = mock(MedicineRepository.class);
        medicineService = new MedicineService(medicineRepository);
    }

    @Test
    void createMedicine_shouldPersistAndMapResponse() {
        MedicineRequest request = new MedicineRequest();
        request.setName("Paracetamol");
        request.setUnit("tablet");
        request.setPrice(BigDecimal.valueOf(1000));

        when(medicineRepository.save(any(Medicine.class))).thenReturn(Medicine.builder()
                .id(1L)
                .name("Paracetamol")
                .unit("tablet")
                .price(BigDecimal.valueOf(1000))
                .build());

        MedicineResponse response = medicineService.createMedicine(request);

        assertEquals(1L, response.getId());
        assertEquals("Paracetamol", response.getName());
        verify(medicineRepository).save(any(Medicine.class));
    }

    @Test
    void getAllMedicines_withSearch_shouldUseNameQuery() {
        when(medicineRepository.findByNameContainingIgnoreCase("para"))
                .thenReturn(List.of(Medicine.builder().id(1L).name("Paracetamol").build()));

        List<MedicineResponse> result = medicineService.getAllMedicines("para");

        assertEquals(1, result.size());
        verify(medicineRepository).findByNameContainingIgnoreCase("para");
    }
}
