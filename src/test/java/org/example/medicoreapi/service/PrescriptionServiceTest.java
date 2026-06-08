package org.example.medicoreapi.service;

import org.example.medicoreapi.dto.request.PrescriptionRequest;
import org.example.medicoreapi.dto.response.PrescriptionResponse;
import org.example.medicoreapi.entity.Appointment;
import org.example.medicoreapi.entity.Doctor;
import org.example.medicoreapi.entity.Medicine;
import org.example.medicoreapi.entity.Patient;
import org.example.medicoreapi.entity.Prescription;
import org.example.medicoreapi.entity.User;
import org.example.medicoreapi.enums.AppointmentStatus;
import org.example.medicoreapi.enums.Role;
import org.example.medicoreapi.repository.AppointmentRepository;
import org.example.medicoreapi.repository.DoctorRepository;
import org.example.medicoreapi.repository.MedicineRepository;
import org.example.medicoreapi.repository.PatientRepository;
import org.example.medicoreapi.repository.PrescriptionDetailRepository;
import org.example.medicoreapi.repository.PrescriptionRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.security.access.AccessDeniedException;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicReference;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class PrescriptionServiceTest {
    private PrescriptionRepository prescriptionRepository;
    private PrescriptionDetailRepository prescriptionDetailRepository;
    private MedicineRepository medicineRepository;
    private AppointmentRepository appointmentRepository;
    private DoctorRepository doctorRepository;
    private PatientRepository patientRepository;
    private PrescriptionService prescriptionService;

    @BeforeEach
    void setUp() {
        prescriptionRepository = mock(PrescriptionRepository.class);
        prescriptionDetailRepository = mock(PrescriptionDetailRepository.class);
        medicineRepository = mock(MedicineRepository.class);
        appointmentRepository = mock(AppointmentRepository.class);
        doctorRepository = mock(DoctorRepository.class);
        patientRepository = mock(PatientRepository.class);
        prescriptionService = new PrescriptionService(
                prescriptionRepository,
                prescriptionDetailRepository,
                medicineRepository,
                appointmentRepository,
                doctorRepository,
                patientRepository
        );
    }

    @Test
    void createPrescription_ownAppointment_shouldCompleteAppointment() {
        User doctorUser = User.builder().id(1L).username("doctor1").role(Role.DOCTOR).enabled(true).build();
        Doctor doctor = Doctor.builder().id(10L).fullName("Dr A").user(doctorUser).build();
        Patient patient = Patient.builder().id(20L).fullName("Patient B").build();
        Appointment appointment = Appointment.builder().id(30L).doctor(doctor).patient(patient).status(AppointmentStatus.CONFIRMED).build();
        Medicine medicine = Medicine.builder().id(40L).name("Amoxicillin").unit("tablet").build();
        PrescriptionRequest request = prescriptionRequest();
        AtomicReference<Prescription> savedPrescription = new AtomicReference<>();

        when(doctorRepository.findByUserUsername("doctor1")).thenReturn(Optional.of(doctor));
        when(appointmentRepository.findById(30L)).thenReturn(Optional.of(appointment));
        when(prescriptionRepository.findByAppointmentId(30L)).thenReturn(Optional.empty());
        when(medicineRepository.findById(40L)).thenReturn(Optional.of(medicine));
        when(prescriptionRepository.save(any(Prescription.class))).thenAnswer(invocation -> {
            Prescription prescription = invocation.getArgument(0);
            prescription.setId(100L);
            savedPrescription.set(prescription);
            return prescription;
        });
        when(prescriptionDetailRepository.findByPrescriptionId(100L)).thenAnswer(invocation -> savedPrescription.get().getDetails());

        PrescriptionResponse response = prescriptionService.createPrescription("doctor1", request);

        assertEquals(100L, response.getId());
        assertEquals("COMPLETED", appointment.getStatus().name());
        assertEquals(1, response.getItems().size());
        assertEquals("Amoxicillin", response.getItems().get(0).getMedicineName());
    }

    @Test
    void createPrescription_otherDoctorAppointment_shouldDeny() {
        Doctor currentDoctor = Doctor.builder().id(10L).fullName("Dr A").build();
        Doctor otherDoctor = Doctor.builder().id(11L).fullName("Dr B").build();
        Appointment appointment = Appointment.builder().id(30L).doctor(otherDoctor).status(AppointmentStatus.CONFIRMED).build();

        when(doctorRepository.findByUserUsername("doctor1")).thenReturn(Optional.of(currentDoctor));
        when(appointmentRepository.findById(30L)).thenReturn(Optional.of(appointment));

        assertThrows(AccessDeniedException.class, () -> prescriptionService.createPrescription("doctor1", prescriptionRequest()));
    }

    private PrescriptionRequest prescriptionRequest() {
        PrescriptionRequest request = new PrescriptionRequest();
        request.setAppointmentId(30L);
        request.setDiagnosis("Influenza");
        PrescriptionRequest.PrescriptionItemRequest item = new PrescriptionRequest.PrescriptionItemRequest();
        item.setMedicineId(40L);
        item.setQuantity(2);
        item.setDosage("1 tablet after meal");
        request.setItems(List.of(item));
        return request;
    }
}
