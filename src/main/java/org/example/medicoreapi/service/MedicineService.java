package org.example.medicoreapi.service;

import org.example.medicoreapi.dto.request.MedicineRequest;
import org.example.medicoreapi.dto.response.MedicineResponse;
import org.example.medicoreapi.entity.Medicine;
import org.example.medicoreapi.exception.ResourceNotFoundException;
import org.example.medicoreapi.repository.MedicineRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.List;

@Service
@Transactional
public class MedicineService {
    private final MedicineRepository medicineRepository;

    public MedicineService(MedicineRepository medicineRepository) {
        this.medicineRepository = medicineRepository;
    }

    public MedicineResponse createMedicine(MedicineRequest request) {
        Medicine medicine = Medicine.builder()
                .name(request.getName())
                .unit(request.getUnit())
                .description(request.getDescription())
                .price(request.getPrice())
                .build();
        return toDto(medicineRepository.save(medicine));
    }

    @Transactional(readOnly = true)
    public List<MedicineResponse> getAllMedicines(String name) {
        List<Medicine> medicines = StringUtils.hasText(name)
                ? medicineRepository.findByNameContainingIgnoreCase(name)
                : medicineRepository.findAll();
        return medicines.stream().map(this::toDto).toList();
    }

    @Transactional(readOnly = true)
    public MedicineResponse getMedicineById(Long id) {
        return toDto(findMedicine(id));
    }

    public MedicineResponse updateMedicine(Long id, MedicineRequest request) {
        Medicine medicine = findMedicine(id);
        medicine.setName(request.getName());
        medicine.setUnit(request.getUnit());
        medicine.setDescription(request.getDescription());
        medicine.setPrice(request.getPrice());
        return toDto(medicineRepository.save(medicine));
    }

    public void deleteMedicine(Long id) {
        if (!medicineRepository.existsById(id)) {
            throw new ResourceNotFoundException("Medicine not found");
        }
        medicineRepository.deleteById(id);
    }

    private Medicine findMedicine(Long id) {
        return medicineRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Medicine not found"));
    }

    private MedicineResponse toDto(Medicine medicine) {
        MedicineResponse response = new MedicineResponse();
        response.setId(medicine.getId());
        response.setName(medicine.getName());
        response.setUnit(medicine.getUnit());
        response.setDescription(medicine.getDescription());
        response.setPrice(medicine.getPrice());
        response.setCreatedAt(medicine.getCreatedAt());
        response.setUpdatedAt(medicine.getUpdatedAt());
        return response;
    }
}
