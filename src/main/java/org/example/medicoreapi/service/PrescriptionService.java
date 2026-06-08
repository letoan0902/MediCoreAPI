package org.example.medicoreapi.service;

import org.example.medicoreapi.dto.request.PrescriptionRequest;
import org.example.medicoreapi.dto.response.PrescriptionResponse;
import org.example.medicoreapi.entity.Appointment;
import org.example.medicoreapi.entity.Doctor;
import org.example.medicoreapi.entity.Medicine;
import org.example.medicoreapi.entity.Prescription;
import org.example.medicoreapi.entity.PrescriptionDetail;
import org.example.medicoreapi.enums.AppointmentStatus;
import org.example.medicoreapi.exception.BadRequestException;
import org.example.medicoreapi.exception.ResourceNotFoundException;
import org.example.medicoreapi.repository.AppointmentRepository;
import org.example.medicoreapi.repository.DoctorRepository;
import org.example.medicoreapi.repository.MedicineRepository;
import org.example.medicoreapi.repository.PatientRepository;
import org.example.medicoreapi.repository.PrescriptionDetailRepository;
import org.example.medicoreapi.repository.PrescriptionRepository;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class PrescriptionService {
    private final PrescriptionRepository prescriptionRepository;
    private final PrescriptionDetailRepository prescriptionDetailRepository;
    private final MedicineRepository medicineRepository;
    private final AppointmentRepository appointmentRepository;
    private final DoctorRepository doctorRepository;
    private final PatientRepository patientRepository;

    public PrescriptionService(
            PrescriptionRepository prescriptionRepository,
            PrescriptionDetailRepository prescriptionDetailRepository,
            MedicineRepository medicineRepository,
            AppointmentRepository appointmentRepository,
            DoctorRepository doctorRepository,
            PatientRepository patientRepository
    ) {
        this.prescriptionRepository = prescriptionRepository;
        this.prescriptionDetailRepository = prescriptionDetailRepository;
        this.medicineRepository = medicineRepository;
        this.appointmentRepository = appointmentRepository;
        this.doctorRepository = doctorRepository;
        this.patientRepository = patientRepository;
    }

    public PrescriptionResponse createPrescription(String doctorUsername, PrescriptionRequest request) {
        Doctor doctor = doctorRepository.findByUserUsername(doctorUsername)
                .orElseThrow(() -> new AccessDeniedException("No doctor profile for current user"));
        Appointment appointment = appointmentRepository.findById(request.getAppointmentId())
                .orElseThrow(() -> new ResourceNotFoundException("Appointment not found"));

        if (appointment.getDoctor() == null || !appointment.getDoctor().getId().equals(doctor.getId())) {
            throw new AccessDeniedException("Doctor can only prescribe for own appointments");
        }
        if (appointment.getPatient() == null) {
            throw new BadRequestException("Appointment has no patient");
        }
        if (appointment.getStatus() == AppointmentStatus.CANCELLED) {
            throw new BadRequestException("Cannot prescribe for a cancelled appointment");
        }
        if (prescriptionRepository.findByAppointmentId(appointment.getId()).isPresent()) {
            throw new BadRequestException("Prescription already exists for this appointment");
        }

        Prescription prescription = Prescription.builder()
                .doctor(doctor)
                .patient(appointment.getPatient())
                .appointment(appointment)
                .diagnosis(request.getDiagnosis())
                .notes(request.getNotes())
                .build();

        List<PrescriptionDetail> details = request.getItems().stream()
                .map(item -> toDetail(item, prescription))
                .toList();
        prescription.getDetails().addAll(details);
        appointment.setStatus(AppointmentStatus.COMPLETED);

        return toDto(prescriptionRepository.save(prescription));
    }

    @Transactional(readOnly = true)
    public PrescriptionResponse getPrescriptionById(Long id, Authentication authentication) {
        Prescription prescription = findPrescription(id);
        ensureCanView(prescription, authentication);
        return toDto(prescription);
    }

    @Transactional(readOnly = true)
    public List<PrescriptionResponse> getPatientPrescriptions(String patientUsername) {
        patientRepository.findByUserUsername(patientUsername)
                .orElseThrow(() -> new AccessDeniedException("No patient profile for current user"));
        return prescriptionRepository.findByPatientUserUsername(patientUsername).stream()
                .map(this::toDto)
                .toList();
    }

    private Prescription findPrescription(Long id) {
        return prescriptionRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Prescription not found"));
    }

    private PrescriptionDetail toDetail(PrescriptionRequest.PrescriptionItemRequest item, Prescription prescription) {
        Medicine medicine = medicineRepository.findById(item.getMedicineId())
                .orElseThrow(() -> new ResourceNotFoundException("Medicine not found"));
        return PrescriptionDetail.builder()
                .prescription(prescription)
                .medicine(medicine)
                .quantity(item.getQuantity())
                .dosage(item.getDosage())
                .notes(item.getNotes())
                .build();
    }

    private void ensureCanView(Prescription prescription, Authentication authentication) {
        String username = authentication.getName();
        boolean admin = hasRole(authentication, "ROLE_ADMIN");
        boolean doctorOwner = prescription.getDoctor() != null
                && prescription.getDoctor().getUser() != null
                && username.equals(prescription.getDoctor().getUser().getUsername());
        boolean patientOwner = prescription.getPatient() != null
                && prescription.getPatient().getUser() != null
                && username.equals(prescription.getPatient().getUser().getUsername());

        if (!admin && !doctorOwner && !patientOwner) {
            throw new AccessDeniedException("Cannot view another user's prescription");
        }
    }

    private boolean hasRole(Authentication authentication, String role) {
        return authentication.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .anyMatch(role::equals);
    }

    private PrescriptionResponse toDto(Prescription prescription) {
        PrescriptionResponse response = new PrescriptionResponse();
        response.setId(prescription.getId());
        response.setDiagnosis(prescription.getDiagnosis());
        response.setNotes(prescription.getNotes());
        response.setCreatedAt(prescription.getCreatedAt());
        if (prescription.getAppointment() != null) {
            response.setAppointmentId(prescription.getAppointment().getId());
        }
        if (prescription.getDoctor() != null) {
            response.setDoctorId(prescription.getDoctor().getId());
            response.setDoctorName(prescription.getDoctor().getFullName());
        }
        if (prescription.getPatient() != null) {
            response.setPatientId(prescription.getPatient().getId());
            response.setPatientName(prescription.getPatient().getFullName());
        }
        response.setItems(prescriptionDetailRepository.findByPrescriptionId(prescription.getId()).stream()
                .map(this::toItemDto)
                .toList());
        return response;
    }

    private PrescriptionResponse.PrescriptionItemResponse toItemDto(PrescriptionDetail detail) {
        PrescriptionResponse.PrescriptionItemResponse item = new PrescriptionResponse.PrescriptionItemResponse();
        if (detail.getMedicine() != null) {
            item.setMedicineId(detail.getMedicine().getId());
            item.setMedicineName(detail.getMedicine().getName());
            item.setUnit(detail.getMedicine().getUnit());
        }
        item.setQuantity(detail.getQuantity());
        item.setDosage(detail.getDosage());
        item.setNotes(detail.getNotes());
        return item;
    }
}
