package org.example.medicoreapi.service;

import org.example.medicoreapi.dto.request.AppointmentRequest;
import org.example.medicoreapi.dto.request.PatientRequest;
import org.example.medicoreapi.dto.response.AppointmentResponse;
import org.example.medicoreapi.dto.response.PatientResponse;
import org.example.medicoreapi.entity.Appointment;
import org.example.medicoreapi.entity.Doctor;
import org.example.medicoreapi.entity.Patient;
import org.example.medicoreapi.entity.User;
import org.example.medicoreapi.enums.AppointmentStatus;
import org.example.medicoreapi.enums.Role;
import org.example.medicoreapi.exception.BadRequestException;
import org.example.medicoreapi.exception.ResourceNotFoundException;
import org.example.medicoreapi.repository.AppointmentRepository;
import org.example.medicoreapi.repository.DoctorRepository;
import org.example.medicoreapi.repository.PatientRepository;
import org.example.medicoreapi.repository.UserRepository;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.List;

@Service
@Transactional
public class PatientService {
    private final PatientRepository patientRepository;
    private final AppointmentRepository appointmentRepository;
    private final DoctorRepository doctorRepository;
    private final UserRepository userRepository;

    public PatientService(
            PatientRepository patientRepository,
            AppointmentRepository appointmentRepository,
            DoctorRepository doctorRepository,
            UserRepository userRepository
    ) {
        this.patientRepository = patientRepository;
        this.appointmentRepository = appointmentRepository;
        this.doctorRepository = doctorRepository;
        this.userRepository = userRepository;
    }

    public PatientResponse createPatient(PatientRequest request) {
        Patient patient = Patient.builder()
                .fullName(request.getFullName())
                .dateOfBirth(request.getDateOfBirth())
                .gender(request.getGender())
                .phone(request.getPhone())
                .address(request.getAddress())
                .user(resolvePatientUser(request.getUsername(), null))
                .build();
        return toDto(patientRepository.save(patient));
    }

    @Transactional(readOnly = true)
    public List<PatientResponse> getAllPatients() {
        return patientRepository.findAll().stream().map(this::toDto).toList();
    }

    @Transactional(readOnly = true)
    public PatientResponse getPatientById(Long id) {
        return toDto(findPatient(id));
    }

    public PatientResponse updatePatient(Long id, PatientRequest request) {
        Patient patient = findPatient(id);
        patient.setFullName(request.getFullName());
        patient.setDateOfBirth(request.getDateOfBirth());
        patient.setGender(request.getGender());
        patient.setPhone(request.getPhone());
        patient.setAddress(request.getAddress());
        if (StringUtils.hasText(request.getUsername())) {
            patient.setUser(resolvePatientUser(request.getUsername(), patient.getId()));
        }
        return toDto(patientRepository.save(patient));
    }

    public void deletePatient(Long id) {
        if (!patientRepository.existsById(id)) {
            throw new ResourceNotFoundException("Patient not found");
        }
        patientRepository.deleteById(id);
    }

    @Transactional(readOnly = true)
    public PatientResponse getMyProfile(String username) {
        return toDto(getPatientForUsername(username));
    }

    public AppointmentResponse bookAppointment(String username, AppointmentRequest request) {
        Patient patient = getPatientForUsername(username);
        Doctor doctor = doctorRepository.findById(request.getDoctorId())
                .orElseThrow(() -> new ResourceNotFoundException("Doctor not found"));

        boolean slotTaken = appointmentRepository
                .findByDoctorIdAndAppointmentDateAndTimeSlot(
                        doctor.getId(),
                        request.getAppointmentDate(),
                        request.getTimeSlot()
                )
                .stream()
                .anyMatch(appointment -> appointment.getStatus() != AppointmentStatus.CANCELLED);

        if (slotTaken) {
            throw new BadRequestException("Time slot already booked");
        }

        Appointment appointment = Appointment.builder()
                .doctor(doctor)
                .patient(patient)
                .appointmentDate(request.getAppointmentDate())
                .timeSlot(request.getTimeSlot())
                .notes(request.getNotes())
                .status(AppointmentStatus.PENDING)
                .build();
        return toAppointmentDto(appointmentRepository.save(appointment));
    }

    @Transactional(readOnly = true)
    public List<AppointmentResponse> getMyAppointments(String username) {
        Patient patient = getPatientForUsername(username);
        return appointmentRepository.findByPatientId(patient.getId()).stream()
                .map(this::toAppointmentDto)
                .toList();
    }

    private Patient findPatient(Long id) {
        return patientRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Patient not found"));
    }

    private Patient getPatientForUsername(String username) {
        return patientRepository.findByUserUsername(username)
                .orElseThrow(() -> new AccessDeniedException("No patient profile for current user"));
    }

    private User resolvePatientUser(String username, Long currentPatientId) {
        if (!StringUtils.hasText(username)) {
            return null;
        }
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("Linked user not found"));
        if (user.getRole() != Role.PATIENT) {
            throw new BadRequestException("Linked user must have PATIENT role");
        }
        patientRepository.findByUserId(user.getId()).ifPresent(existing -> {
            if (!existing.getId().equals(currentPatientId)) {
                throw new BadRequestException("User is already linked to another patient profile");
            }
        });
        return user;
    }

    private PatientResponse toDto(Patient patient) {
        PatientResponse response = new PatientResponse();
        response.setId(patient.getId());
        response.setFullName(patient.getFullName());
        response.setDateOfBirth(patient.getDateOfBirth());
        response.setGender(patient.getGender());
        response.setPhone(patient.getPhone());
        response.setAddress(patient.getAddress());
        response.setCreatedAt(patient.getCreatedAt());
        response.setUpdatedAt(patient.getUpdatedAt());
        if (patient.getUser() != null) {
            response.setUserId(patient.getUser().getId());
            response.setUsername(patient.getUser().getUsername());
        }
        return response;
    }

    private AppointmentResponse toAppointmentDto(Appointment appointment) {
        AppointmentResponse response = new AppointmentResponse();
        response.setId(appointment.getId());
        response.setAppointmentDate(appointment.getAppointmentDate());
        response.setTimeSlot(appointment.getTimeSlot());
        response.setStatus(appointment.getStatus() == null ? null : appointment.getStatus().name());
        response.setNotes(appointment.getNotes());
        if (appointment.getPatient() != null) {
            response.setPatientId(appointment.getPatient().getId());
            response.setPatientName(appointment.getPatient().getFullName());
        }
        if (appointment.getDoctor() != null) {
            response.setDoctorId(appointment.getDoctor().getId());
            response.setDoctorName(appointment.getDoctor().getFullName());
        }
        return response;
    }
}
