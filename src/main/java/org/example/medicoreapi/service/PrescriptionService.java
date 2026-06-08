package org.example.medicoreapi.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.medicoreapi.dto.request.PrescriptionRequest;
import org.example.medicoreapi.dto.response.PrescriptionResponse;
import org.example.medicoreapi.entity.*;
import org.example.medicoreapi.exception.ResourceNotFoundException;
import org.example.medicoreapi.repository.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

/**
 * ===================================================================
 * SERVICE: PrescriptionService (Nghiệp vụ Đơn thuốc)
 * NGƯỜI LÀM: Người 5 - Trần Đăng Việt (Medicine + Prescription)
 * ===================================================================
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class PrescriptionService {

    private final PrescriptionRepository prescriptionRepository;
    private final PrescriptionDetailRepository detailRepository;
    private final MedicineRepository medicineRepository;
    private final AppointmentRepository appointmentRepository;
    private final DoctorRepository doctorRepository;
    private final PatientRepository patientRepository;

    @Transactional
    public PrescriptionResponse createPrescription(Long doctorId, PrescriptionRequest request) {
        log.info("Doctor ID {} is creating a prescription for appointment ID {}", doctorId, request.getAppointmentId());

        Appointment appointment = appointmentRepository.findById(request.getAppointmentId())
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy lịch hẹn"));

        Doctor doctor = doctorRepository.findById(doctorId)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy bác sĩ"));

        Prescription prescription = Prescription.builder()
                .diagnosis(request.getDiagnosis())
                .notes(request.getNotes())
                .doctor(doctor)
                .patient(appointment.getPatient())
                .appointment(appointment)
                .build();

        Prescription savedPrescription = prescriptionRepository.save(prescription);

        List<PrescriptionDetail> details = request.getItems().stream()
                .map(item -> {
                    Medicine medicine = medicineRepository.findById(item.getMedicineId())
                            .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy thuốc ID: " + item.getMedicineId()));
                    
                    return PrescriptionDetail.builder()
                            .prescription(savedPrescription)
                            .medicine(medicine)
                            .quantity(item.getQuantity())
                            .dosage(item.getDosage())
                            .notes(item.getNotes())
                            .build();
                })
                .collect(Collectors.toList());

        detailRepository.saveAll(details);
        savedPrescription.setPrescriptionDetails(details);
        return mapToResponse(savedPrescription);
    }

    public PrescriptionResponse getPrescriptionById(Long id) {
        log.info("Fetching prescription details for ID: {}", id);
        Prescription prescription = prescriptionRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy đơn thuốc ID: " + id));
        
        return mapToResponse(prescription);
    }

    public List<PrescriptionResponse> getPatientPrescriptions(Long patientId) {
        log.info("Fetching prescription history for patient ID: {}", patientId);
        return prescriptionRepository.findByPatientId(patientId).stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    public List<PrescriptionResponse> getDoctorPrescriptions(Long doctorId) {
        log.info("Fetching prescription history for doctor ID: {}", doctorId);
        return prescriptionRepository.findByDoctorId(doctorId).stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    private PrescriptionResponse mapToResponse(Prescription prescription) {
        List<PrescriptionResponse.PrescriptionItemResponse> items = prescription.getPrescriptionDetails().stream()
                .map(detail -> PrescriptionResponse.PrescriptionItemResponse.builder()
                        .medicineName(detail.getMedicine().getName())
                        .quantity(detail.getQuantity())
                        .dosage(detail.getDosage())
                        .notes(detail.getNotes())
                        .build())
                .collect(Collectors.toList());

        return PrescriptionResponse.builder()
                .id(prescription.getId())
                .doctorName(prescription.getDoctor().getFullName())
                .patientName(prescription.getPatient().getFullName())
                .diagnosis(prescription.getDiagnosis())
                .notes(prescription.getNotes())
                .createdAt(prescription.getCreatedAt())
                .items(items)
                .build();
    }
}
