package org.example.medicoreapi.repository;

import org.example.medicoreapi.entity.Doctor;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface DoctorRepository extends JpaRepository<Doctor, Long> {
	Optional<Doctor> findByUserUsername(String username);

	Optional<Doctor> findByUserId(Long userId);

	boolean existsByUserId(Long userId);
}

