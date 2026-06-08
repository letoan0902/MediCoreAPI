package org.example.medicoreapi.service;

import org.example.medicoreapi.dto.request.AppointmentRequest;
import org.example.medicoreapi.dto.response.AppointmentResponse;
import org.example.medicoreapi.entity.Appointment;
import org.example.medicoreapi.entity.Doctor;
import org.example.medicoreapi.entity.Patient;
import org.example.medicoreapi.entity.User;
import org.example.medicoreapi.enums.AppointmentStatus;
import org.example.medicoreapi.enums.Role;
import org.example.medicoreapi.exception.BadRequestException;
import org.example.medicoreapi.repository.AppointmentRepository;
import org.example.medicoreapi.repository.DoctorRepository;
import org.example.medicoreapi.repository.PatientRepository;
import org.example.medicoreapi.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class PatientServiceTest {
    private PatientRepository patientRepository;
    private AppointmentRepository appointmentRepository;
    private DoctorRepository doctorRepository;
    private UserRepository userRepository;
    private PatientService patientService;

    @BeforeEach
    void setUp() {
        patientRepository = mock(PatientRepository.class);
        appointmentRepository = mock(AppointmentRepository.class);
        doctorRepository = mock(DoctorRepository.class);
        userRepository = mock(UserRepository.class);
        patientService = new PatientService(patientRepository, appointmentRepository, doctorRepository, userRepository);
    }

    @Test
    void bookAppointment_availableSlot_shouldCreatePendingAppointment() {
        User patientUser = User.builder().id(1L).username("patient1").role(Role.PATIENT).enabled(true).build();
        Patient patient = Patient.builder().id(10L).fullName("Patient A").user(patientUser).build();
        Doctor doctor = Doctor.builder().id(20L).fullName("Dr B").build();
        AppointmentRequest request = appointmentRequest();

        when(patientRepository.findByUserUsername("patient1")).thenReturn(Optional.of(patient));
        when(doctorRepository.findById(20L)).thenReturn(Optional.of(doctor));
        when(appointmentRepository.findByDoctorIdAndAppointmentDateAndTimeSlot(20L, request.getAppointmentDate(), "09:00"))
                .thenReturn(List.of());
        when(appointmentRepository.save(any(Appointment.class))).thenAnswer(invocation -> {
            Appointment appointment = invocation.getArgument(0);
            appointment.setId(99L);
            return appointment;
        });

        AppointmentResponse response = patientService.bookAppointment("patient1", request);

        assertEquals(99L, response.getId());
        assertEquals("PENDING", response.getStatus());
        assertEquals("Patient A", response.getPatientName());
    }

    @Test
    void bookAppointment_takenSlot_shouldThrowBadRequest() {
        Patient patient = Patient.builder().id(10L).fullName("Patient A").build();
        Doctor doctor = Doctor.builder().id(20L).fullName("Dr B").build();
        AppointmentRequest request = appointmentRequest();
        Appointment existing = Appointment.builder().id(1L).status(AppointmentStatus.CONFIRMED).build();

        when(patientRepository.findByUserUsername("patient1")).thenReturn(Optional.of(patient));
        when(doctorRepository.findById(20L)).thenReturn(Optional.of(doctor));
        when(appointmentRepository.findByDoctorIdAndAppointmentDateAndTimeSlot(20L, request.getAppointmentDate(), "09:00"))
                .thenReturn(List.of(existing));

        assertThrows(BadRequestException.class, () -> patientService.bookAppointment("patient1", request));
    }

    private AppointmentRequest appointmentRequest() {
        AppointmentRequest request = new AppointmentRequest();
        request.setDoctorId(20L);
        request.setAppointmentDate(LocalDate.now().plusDays(1));
        request.setTimeSlot("09:00");
        return request;
    }
}
