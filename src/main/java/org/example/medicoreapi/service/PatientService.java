package org.example.medicoreapi.service;

import lombok.RequiredArgsConstructor;
import org.example.medicoreapi.dto.request.AppointmentRequest;
import org.example.medicoreapi.dto.request.PatientRequest;
import org.example.medicoreapi.dto.response.AppointmentResponse;
import org.example.medicoreapi.dto.response.PatientResponse;
import org.example.medicoreapi.entity.Appointment;
import org.example.medicoreapi.entity.Doctor;
import org.example.medicoreapi.entity.Patient;
import org.example.medicoreapi.enums.AppointmentStatus;
import org.example.medicoreapi.exception.BadRequestException;
import org.example.medicoreapi.exception.ResourceNotFoundException;
import org.example.medicoreapi.repository.AppointmentRepository;
import org.example.medicoreapi.repository.DoctorRepository;
import org.example.medicoreapi.repository.PatientRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class PatientService {

    private final PatientRepository patientRepository;
    private final AppointmentRepository appointmentRepository;
    private final DoctorRepository doctorRepository;

    public PatientResponse createPatient(PatientRequest request) {
        Patient patient = Patient.builder()
                .fullName(request.getFullName())
                .dateOfBirth(request.getDateOfBirth())
                .gender(request.getGender())
                .phone(request.getPhone())
                .address(request.getAddress())
                .build();

        Patient savedPatient = patientRepository.save(patient);
        return mapToPatientResponse(savedPatient);
    }

    public List<PatientResponse> getAllPatients() {
        return patientRepository.findAll()
                .stream()
                .map(this::mapToPatientResponse)
                .toList();
    }

    public PatientResponse getPatientById(Long id) {
        Patient patient = findPatientById(id);
        return mapToPatientResponse(patient);
    }

    public PatientResponse updatePatient(Long id, PatientRequest request) {
        Patient patient = findPatientById(id);

        patient.setFullName(request.getFullName());
        patient.setDateOfBirth(request.getDateOfBirth());
        patient.setGender(request.getGender());
        patient.setPhone(request.getPhone());
        patient.setAddress(request.getAddress());

        Patient updatedPatient = patientRepository.save(patient);
        return mapToPatientResponse(updatedPatient);
    }

    public void deletePatient(Long id) {
        Patient patient = findPatientById(id);
        patientRepository.delete(patient);
    }

    public PatientResponse getMyProfile(Long patientUserId) {
        Patient patient = patientRepository.findByUserId(patientUserId)
                .orElseThrow(() -> new ResourceNotFoundException("Patient profile not found"));

        return mapToPatientResponse(patient);
    }

    public AppointmentResponse bookAppointment(Long patientUserId, AppointmentRequest request) {
        Patient patient = patientRepository.findByUserId(patientUserId)
                .orElseThrow(() -> new ResourceNotFoundException("Patient profile not found"));

        Doctor doctor = doctorRepository.findById(request.getDoctorId())
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Doctor not found with id: " + request.getDoctorId()
                ));

        boolean existedAppointment = appointmentRepository.existsByDoctorIdAndAppointmentDateAndTimeSlot(
                request.getDoctorId(),
                request.getAppointmentDate(),
                request.getTimeSlot()
        );

        if (existedAppointment) {
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

        Appointment savedAppointment = appointmentRepository.save(appointment);
        return mapToAppointmentResponse(savedAppointment);
    }

    public List<AppointmentResponse> getMyAppointments(Long patientUserId) {
        Patient patient = patientRepository.findByUserId(patientUserId)
                .orElseThrow(() -> new ResourceNotFoundException("Patient profile not found"));

        return appointmentRepository.findByPatientId(patient.getId())
                .stream()
                .map(this::mapToAppointmentResponse)
                .toList();
    }

    private Patient findPatientById(Long id) {
        return patientRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Patient not found with id: " + id
                ));
    }

    private PatientResponse mapToPatientResponse(Patient patient) {
        return PatientResponse.builder()
                .id(patient.getId())
                .fullName(patient.getFullName())
                .dateOfBirth(patient.getDateOfBirth())
                .gender(patient.getGender())
                .phone(patient.getPhone())
                .address(patient.getAddress())
                .build();
    }

    private AppointmentResponse mapToAppointmentResponse(Appointment appointment) {
        return AppointmentResponse.builder()
                .id(appointment.getId())
                .doctorName(appointment.getDoctor().getFullName())
                .patientName(appointment.getPatient().getFullName())
                .appointmentDate(appointment.getAppointmentDate())
                .timeSlot(appointment.getTimeSlot())
                .status(appointment.getStatus())
                .notes(appointment.getNotes())
                .build();
    }
}