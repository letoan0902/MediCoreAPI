package org.example.medicoreapi.service;

import org.example.medicoreapi.dto.request.DoctorRequest;
import org.example.medicoreapi.dto.response.AppointmentResponse;
import org.example.medicoreapi.dto.response.DoctorResponse;
import org.example.medicoreapi.entity.Appointment;
import org.example.medicoreapi.entity.Doctor;
import org.example.medicoreapi.entity.User;
import org.example.medicoreapi.enums.Role;
import org.example.medicoreapi.exception.BadRequestException;
import org.example.medicoreapi.exception.ResourceNotFoundException;
import org.example.medicoreapi.repository.AppointmentRepository;
import org.example.medicoreapi.repository.DoctorRepository;
import org.example.medicoreapi.repository.UserRepository;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.security.Principal;
import java.time.LocalDate;
import java.util.List;

@Service
@Transactional
public class DoctorService {
	private final DoctorRepository doctorRepository;
	private final AppointmentRepository appointmentRepository;
	private final UserRepository userRepository;

	public DoctorService(DoctorRepository doctorRepository, AppointmentRepository appointmentRepository, UserRepository userRepository) {
		this.doctorRepository = doctorRepository;
		this.appointmentRepository = appointmentRepository;
		this.userRepository = userRepository;
	}

	public DoctorResponse createDoctor(DoctorRequest request) {
		User linkedUser = resolveDoctorUser(request.getUsername(), null);
		Doctor d = Doctor.builder()
				.fullName(request.getFullName())
				.specialization(request.getSpecialization())
				.phone(request.getPhone())
				.email(request.getEmail())
				.user(linkedUser)
				.build();
		Doctor saved = doctorRepository.save(d);
		return toDto(saved);
	}

	@Transactional(readOnly = true)
	public List<DoctorResponse> getAllDoctors() {
		return doctorRepository.findAll().stream().map(this::toDto).toList();
	}

	@Transactional(readOnly = true)
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
		if (StringUtils.hasText(request.getUsername())) {
			d.setUser(resolveDoctorUser(request.getUsername(), d.getId()));
		}
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
		return getMyAppointments(principal.getName());
	}

	@Transactional(readOnly = true)
	public List<AppointmentResponse> getMyAppointments(String username) {
		Doctor doctor = doctorRepository.findByUserUsername(username)
				.orElseThrow(() -> new AccessDeniedException("No doctor profile for current user"));
		List<Appointment> apps = appointmentRepository.findByDoctorId(doctor.getId());
		return apps.stream().map(this::toAppointmentDto).toList();
	}

	public List<AppointmentResponse> getMyTodayAppointments(Principal principal) {
		return getMyTodayAppointments(principal.getName());
	}

	@Transactional(readOnly = true)
	public List<AppointmentResponse> getMyTodayAppointments(String username) {
		Doctor doctor = doctorRepository.findByUserUsername(username)
				.orElseThrow(() -> new AccessDeniedException("No doctor profile for current user"));
		LocalDate today = LocalDate.now();
		List<Appointment> apps = appointmentRepository.findByDoctorIdAndAppointmentDate(doctor.getId(), today);
		return apps.stream().map(this::toAppointmentDto).toList();
	}

	private DoctorResponse toDto(Doctor d) {
		DoctorResponse r = new DoctorResponse();
		r.setId(d.getId());
		if (d.getUser() != null) {
			r.setUserId(d.getUser().getId());
			r.setUsername(d.getUser().getUsername());
		}
		r.setFullName(d.getFullName());
		r.setSpecialization(d.getSpecialization());
		r.setPhone(d.getPhone());
		r.setEmail(d.getEmail());
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
		if (a.getPatient() != null) {
			r.setPatientId(a.getPatient().getId());
			r.setPatientName(a.getPatient().getFullName());
		}
		if (a.getDoctor() != null) {
			r.setDoctorId(a.getDoctor().getId());
			r.setDoctorName(a.getDoctor().getFullName());
		}
		return r;
	}

	private User resolveDoctorUser(String username, Long currentDoctorId) {
		if (!StringUtils.hasText(username)) {
			return null;
		}
		User user = userRepository.findByUsername(username)
				.orElseThrow(() -> new ResourceNotFoundException("Linked user not found"));
		if (user.getRole() != Role.DOCTOR) {
			throw new BadRequestException("Linked user must have DOCTOR role");
		}
		doctorRepository.findByUserId(user.getId()).ifPresent(existing -> {
			if (!existing.getId().equals(currentDoctorId)) {
				throw new BadRequestException("User is already linked to another doctor profile");
			}
		});
		return user;
	}
}

