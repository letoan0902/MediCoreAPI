package org.example.medicoreapi.service;

import org.example.medicoreapi.dto.request.MedicineRequest;
import org.example.medicoreapi.dto.response.MedicineResponse;
import org.example.medicoreapi.entity.Medicine;
import org.example.medicoreapi.exception.ResourceNotFoundException;
import org.example.medicoreapi.repository.MedicineRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;


@ExtendWith(MockitoExtension.class)
public class MedicineServiceTest {

    @Mock
    private MedicineRepository medicineRepository;

    @InjectMocks
    private MedicineService medicineService;

    private Medicine medicine;
    private MedicineRequest request;

    @BeforeEach
    void setUp() {
        medicine = Medicine.builder()
                .id(1L)
                .name("Paracetamol")
                .unit("Viên")
                .price(new BigDecimal("1000"))
                .build();

        request = MedicineRequest.builder()
                .name("Paracetamol")
                .unit("Viên")
                .price(new BigDecimal("1000"))
                .build();
    }

    @Test
    void createMedicine_Success() {
        when(medicineRepository.save(any(Medicine.class))).thenReturn(medicine);

        MedicineResponse response = medicineService.createMedicine(request);

        assertNotNull(response);
        assertEquals("Paracetamol", response.getName());
        verify(medicineRepository, times(1)).save(any(Medicine.class));
    }

    @Test
    void getAllMedicines_ReturnsList() {
        when(medicineRepository.findAll()).thenReturn(Collections.singletonList(medicine));

        List<MedicineResponse> response = medicineService.getAllMedicines();

        assertFalse(response.isEmpty());
        assertEquals(1, response.size());
        verify(medicineRepository, times(1)).findAll();
    }

    @Test
    void getMedicineById_Success() {
        when(medicineRepository.findById(1L)).thenReturn(Optional.of(medicine));

        MedicineResponse response = medicineService.getMedicineById(1L);

        assertNotNull(response);
        assertEquals(1L, response.getId());
    }

    @Test
    void getMedicineById_NotFound_ThrowsException() {
        when(medicineRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> medicineService.getMedicineById(1L));
    }

    @Test
    void updateMedicine_Success() {
        when(medicineRepository.findById(1L)).thenReturn(Optional.of(medicine));
        when(medicineRepository.save(any(Medicine.class))).thenReturn(medicine);

        MedicineResponse response = medicineService.updateMedicine(1L, request);

        assertNotNull(response);
        verify(medicineRepository, times(1)).save(any(Medicine.class));
    }

    @Test
    void deleteMedicine_Success() {
        when(medicineRepository.existsById(1L)).thenReturn(true);
        doNothing().when(medicineRepository).deleteById(1L);

        assertDoesNotThrow(() -> medicineService.deleteMedicine(1L));
        verify(medicineRepository, times(1)).deleteById(1L);
    }
}
