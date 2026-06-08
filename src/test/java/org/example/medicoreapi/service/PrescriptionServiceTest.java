package org.example.medicoreapi.service;

import org.example.medicoreapi.dto.request.PrescriptionRequest;
import org.example.medicoreapi.dto.response.PrescriptionResponse;
import org.example.medicoreapi.entity.*;
import org.example.medicoreapi.exception.ResourceNotFoundException;
import org.example.medicoreapi.repository.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class PrescriptionServiceTest {

    @Mock
    private PrescriptionRepository prescriptionRepository;
    @Mock
    private PrescriptionDetailRepository detailRepository;
    @Mock
    private MedicineRepository medicineRepository;
    @Mock
    private AppointmentRepository appointmentRepository;
    @Mock
    private DoctorRepository doctorRepository;
    @Mock
    private PatientRepository patientRepository;

    @InjectMocks
    private PrescriptionService prescriptionService;

    private Prescription prescription;
    private PrescriptionRequest request;
    private Appointment appointment;
    private Doctor doctor;
    private Patient patient;
    private Medicine medicine;

    @BeforeEach
    void setUp() {
        doctor = Doctor.builder().id(1L).fullName("Dr. Minh").build();
        patient = Patient.builder().id(1L).fullName("Patient Vuong").build();
        
        appointment = Appointment.builder()
                .id(1L)
                .doctor(doctor)
                .patient(patient)
                .build();

        medicine = Medicine.builder()
                .id(1L)
                .name("Antibiotic")
                .unit("Vỉ")
                .build();

        request = PrescriptionRequest.builder()
                .appointmentId(1L)
                .diagnosis("Cảm cúm")
                .notes("Nghỉ ngơi nhiều")
                .items(Collections.singletonList(
                        PrescriptionRequest.PrescriptionItemRequest.builder()
                                .medicineId(1L)
                                .quantity(1)
                                .dosage("1 viên/ngày")
                                .build()
                ))
                .build();

        prescription = Prescription.builder()
                .id(1L)
                .doctor(doctor)
                .patient(patient)
                .appointment(appointment)
                .diagnosis("Cảm cúm")
                .createdAt(LocalDateTime.now())
                .prescriptionDetails(new ArrayList<>())
                .build();
    }

    @Test
    void createPrescription_Success() {
        when(appointmentRepository.findById(1L)).thenReturn(Optional.of(appointment));
        when(doctorRepository.findById(1L)).thenReturn(Optional.of(doctor));
        when(medicineRepository.findById(1L)).thenReturn(Optional.of(medicine));
        when(prescriptionRepository.save(any(Prescription.class))).thenReturn(prescription);

        PrescriptionResponse response = prescriptionService.createPrescription(1L, request);

        assertNotNull(response);
        assertEquals("Cảm cúm", response.getDiagnosis());
        verify(prescriptionRepository, times(1)).save(any(Prescription.class));
        verify(detailRepository, times(1)).saveAll(anyList());
    }

    @Test
    void createPrescription_AppointmentNotFound_ThrowsException() {
        when(appointmentRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> prescriptionService.createPrescription(1L, request));
    }

    @Test
    void getPrescriptionById_Success() {
        PrescriptionDetail detail = PrescriptionDetail.builder()
                .medicine(medicine)
                .quantity(1)
                .dosage("1 viên/ngày")
                .build();
        prescription.setPrescriptionDetails(Collections.singletonList(detail));

        when(prescriptionRepository.findById(1L)).thenReturn(Optional.of(prescription));

        PrescriptionResponse response = prescriptionService.getPrescriptionById(1L);

        assertNotNull(response);
        assertEquals(1L, response.getId());
        assertEquals("Dr. Minh", response.getDoctorName());
    }

    @Test
    void getPatientPrescriptions_ReturnsList() {
        when(prescriptionRepository.findByPatientId(1L)).thenReturn(Collections.singletonList(prescription));

        List<PrescriptionResponse> response = prescriptionService.getPatientPrescriptions(1L);

        assertNotNull(response);
        assertFalse(response.isEmpty());
        verify(prescriptionRepository, times(1)).findByPatientId(1L);
    }
}
