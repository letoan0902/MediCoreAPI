package org.example.medicoreapi.repository;

import org.example.medicoreapi.entity.Prescription;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface PrescriptionRepository extends JpaRepository<Prescription, Long> {
    List<Prescription> findByPatientId(Long patientId);

    List<Prescription> findByDoctorId(Long doctorId);

    List<Prescription> findByPatientUserUsername(String username);

    List<Prescription> findByDoctorUserUsername(String username);

    Optional<Prescription> findByAppointmentId(Long appointmentId);
}
