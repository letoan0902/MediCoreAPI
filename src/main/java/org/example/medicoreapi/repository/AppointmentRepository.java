package org.example.medicoreapi.repository;

import org.example.medicoreapi.entity.Appointment;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;

public interface AppointmentRepository extends JpaRepository<Appointment, Long> {

    List<Appointment> findByDoctorId(Long doctorId);

    List<Appointment> findByPatientId(Long patientId);

    List<Appointment> findByDoctorIdAndAppointmentDate(Long doctorId, LocalDate appointmentDate);

    boolean existsByDoctorIdAndAppointmentDateAndTimeSlot(
            Long doctorId,
            LocalDate appointmentDate,
            String timeSlot
    );
}