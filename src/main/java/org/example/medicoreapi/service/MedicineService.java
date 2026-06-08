package org.example.medicoreapi.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.medicoreapi.dto.request.MedicineRequest;
import org.example.medicoreapi.dto.response.MedicineResponse;
import org.example.medicoreapi.entity.Medicine;
import org.example.medicoreapi.exception.ResourceNotFoundException;
import org.example.medicoreapi.repository.MedicineRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class MedicineService {

    private final MedicineRepository medicineRepository;

    @Transactional
    public MedicineResponse createMedicine(MedicineRequest request) {
        log.info("Creating new medicine: {}", request.getName());
        Medicine medicine = Medicine.builder()
                .name(request.getName())
                .unit(request.getUnit())
                .description(request.getDescription())
                .price(request.getPrice())
                .build();
        
        Medicine saved = medicineRepository.save(medicine);
        return mapToResponse(saved);
    }

    public List<MedicineResponse> getAllMedicines() {
        log.info("Fetching all medicines");
        return medicineRepository.findAll().stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    public MedicineResponse getMedicineById(Long id) {
        log.info("Fetching medicine with id: {}", id);
        return medicineRepository.findById(id)
                .map(this::mapToResponse)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy thuốc với id: " + id));
    }

    @Transactional
    public MedicineResponse updateMedicine(Long id, MedicineRequest request) {
        log.info("Updating medicine with id: {}", id);
        Medicine medicine = medicineRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy thuốc với id: " + id));

        medicine.setName(request.getName());
        medicine.setUnit(request.getUnit());
        medicine.setDescription(request.getDescription());
        medicine.setPrice(request.getPrice());

        Medicine updated = medicineRepository.save(medicine);
        return mapToResponse(updated);
    }

    @Transactional
    public void deleteMedicine(Long id) {
        log.info("Deleting medicine with id: {}", id);
        if (!medicineRepository.existsById(id)) {
            throw new ResourceNotFoundException("Không tìm thấy thuốc với id: " + id);
        }
        medicineRepository.deleteById(id);
    }

    private MedicineResponse mapToResponse(Medicine medicine) {
        return MedicineResponse.builder()
                .id(medicine.getId())
                .name(medicine.getName())
                .unit(medicine.getUnit())
                .description(medicine.getDescription())
                .price(medicine.getPrice())
                .createdAt(medicine.getCreatedAt())
                .updatedAt(medicine.getUpdatedAt())
                .build();
    }
}
