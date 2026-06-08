package org.example.medicoreapi.service;

import org.example.medicoreapi.dto.request.DoctorRequest;
import org.example.medicoreapi.dto.response.AppointmentResponse;
import org.example.medicoreapi.dto.response.DoctorResponse;
import org.example.medicoreapi.entity.Appointment;
import org.example.medicoreapi.entity.Doctor;
import org.example.medicoreapi.entity.Patient;
import org.example.medicoreapi.entity.User;
import org.example.medicoreapi.enums.AppointmentStatus;
import org.example.medicoreapi.enums.Role;
import org.example.medicoreapi.repository.AppointmentRepository;
import org.example.medicoreapi.repository.DoctorRepository;
import org.example.medicoreapi.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.security.Principal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

class DoctorServiceTest {
	private DoctorRepository doctorRepository;
	private AppointmentRepository appointmentRepository;
	private UserRepository userRepository;
	private DoctorService doctorService;

	@BeforeEach
	void setUp() {
		doctorRepository = mock(DoctorRepository.class);
		appointmentRepository = mock(AppointmentRepository.class);
		userRepository = mock(UserRepository.class);
		doctorService = new DoctorService(doctorRepository, appointmentRepository, userRepository);
	}

	@Test
	void createDoctor_shouldReturnCreatedDoctor() {
		DoctorRequest req = new DoctorRequest();
		req.setFullName("Dr. A");
		req.setSpecialization("Cardiology");
		req.setEmail("a@example.com");
		req.setPhone("0123456789");

		Doctor saved = Doctor.builder()
				.id(1L)
				.fullName(req.getFullName())
				.specialization(req.getSpecialization())
				.email(req.getEmail())
				.phone(req.getPhone())
				.createdAt(LocalDateTime.now())
				.build();

		when(doctorRepository.save(any(Doctor.class))).thenReturn(saved);

		DoctorResponse resp = doctorService.createDoctor(req);

		assertNotNull(resp);
		assertEquals(1L, resp.getId());
		assertEquals("Dr. A", resp.getFullName());
		verify(doctorRepository, times(1)).save(any(Doctor.class));
	}

	@Test
	void getMyTodayAppointments_shouldReturnOnlyToday() {
		User doctorUser = User.builder().id(3L).username("doc1").role(Role.DOCTOR).enabled(true).build();
		Doctor doctor = Doctor.builder().id(2L).user(doctorUser).fullName("Dr X").build();
		Patient patient = Patient.builder().id(4L).fullName("P1").build();
		when(doctorRepository.findByUserUsername("doc1")).thenReturn(Optional.of(doctor));

		Appointment a1 = Appointment.builder()
				.id(11L)
				.appointmentDate(LocalDate.now())
				.timeSlot("09:00")
				.status(AppointmentStatus.CONFIRMED)
				.patient(patient)
				.doctor(doctor)
				.build();

		when(appointmentRepository.findByDoctorIdAndAppointmentDate(eq(2L), any(LocalDate.class))).thenReturn(List.of(a1));

		Principal p = () -> "doc1";
		List<AppointmentResponse> list = doctorService.getMyTodayAppointments(p);

		assertEquals(1, list.size());
		assertEquals(a1.getId(), list.get(0).getId());
	}
}

