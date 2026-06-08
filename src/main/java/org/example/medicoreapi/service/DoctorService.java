package org.example.medicoreapi.service;

import org.example.medicoreapi.dto.request.DoctorRequest;
import org.example.medicoreapi.dto.response.AppointmentResponse;
import org.example.medicoreapi.dto.response.DoctorResponse;
import org.example.medicoreapi.entity.Appointment;
import org.example.medicoreapi.entity.Doctor;
import org.example.medicoreapi.exception.ResourceNotFoundException;
import org.example.medicoreapi.repository.AppointmentRepository;
import org.example.medicoreapi.repository.DoctorRepository;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.security.Principal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class DoctorService {
	private final DoctorRepository doctorRepository;
	private final AppointmentRepository appointmentRepository;

	public DoctorService(DoctorRepository doctorRepository, AppointmentRepository appointmentRepository) {
		this.doctorRepository = doctorRepository;
		this.appointmentRepository = appointmentRepository;
	}

	public DoctorResponse createDoctor(DoctorRequest request) {
		Doctor d = Doctor.builder()
				.fullName(request.getFullName())
				.specialization(request.getSpecialization())
				.phone(request.getPhone())
				.email(request.getEmail())
				.username(request.getUsername())
				.createdAt(LocalDateTime.now())
				.build();
		Doctor saved = doctorRepository.save(d);
		return toDto(saved);
	}

	public List<DoctorResponse> getAllDoctors() {
		return doctorRepository.findAll().stream().map(this::toDto).collect(Collectors.toList());
	}

	public DoctorResponse getDoctorById(Long id) {
		Doctor d = doctorRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Doctor not found"));
		return toDto(d);
	}

	public DoctorResponse updateDoctor(Long id, DoctorRequest request) {
		Doctor d = doctorRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Doctor not found"));
		d.setFullName(request.getFullName());
		d.setSpecialization(request.getSpecialization());
		d.setPhone(request.getPhone());
		d.setEmail(request.getEmail());
		if (request.getUsername() != null) d.setUsername(request.getUsername());
		d.setUpdatedAt(LocalDateTime.now());
		Doctor saved = doctorRepository.save(d);
		return toDto(saved);
	}

	public void deleteDoctor(Long id) {
		if (!doctorRepository.existsById(id)) {
			throw new ResourceNotFoundException("Doctor not found");
		}
		doctorRepository.deleteById(id);
	}

	public List<AppointmentResponse> getMyAppointments(Principal principal) {
		String username = principal.getName();
		Doctor doctor = doctorRepository.findByUsername(username).orElseThrow(() -> new AccessDeniedException("No doctor profile for current user"));
		List<Appointment> apps = appointmentRepository.findByDoctorId(doctor.getId());
		return apps.stream().map(this::toAppointmentDto).collect(Collectors.toList());
	}

	public List<AppointmentResponse> getMyTodayAppointments(Principal principal) {
		String username = principal.getName();
		Doctor doctor = doctorRepository.findByUsername(username).orElseThrow(() -> new AccessDeniedException("No doctor profile for current user"));
		LocalDate today = LocalDate.now();
		List<Appointment> apps = appointmentRepository.findByDoctorIdAndAppointmentDate(doctor.getId(), today);
		return apps.stream().map(this::toAppointmentDto).collect(Collectors.toList());
	}

	private DoctorResponse toDto(Doctor d) {
		DoctorResponse r = new DoctorResponse();
		r.setId(d.getId());
		r.setFullName(d.getFullName());
		r.setSpecialization(d.getSpecialization());
		r.setPhone(d.getPhone());
		r.setEmail(d.getEmail());
		r.setUsername(d.getUsername());
		r.setCreatedAt(d.getCreatedAt());
		r.setUpdatedAt(d.getUpdatedAt());
		return r;
	}

	private AppointmentResponse toAppointmentDto(Appointment a) {
		AppointmentResponse r = new AppointmentResponse();
		r.setId(a.getId());
		r.setAppointmentDate(a.getAppointmentDate());
		r.setTimeSlot(a.getTimeSlot());
		r.setStatus(a.getStatus() != null ? a.getStatus().name() : null);
		r.setNotes(a.getNotes());
		r.setPatientName(a.getPatientName());
		if (a.getDoctor() != null) {
			r.setDoctorId(a.getDoctor().getId());
			r.setDoctorName(a.getDoctor().getFullName());
		}
		return r;
	}
}

