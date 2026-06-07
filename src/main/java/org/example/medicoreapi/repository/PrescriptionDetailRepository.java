package org.example.medicoreapi.repository;

import org.example.medicoreapi.entity.PrescriptionDetail;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PrescriptionDetailRepository extends JpaRepository<PrescriptionDetail, Long> {
    List<PrescriptionDetail> findByPrescriptionId(Long prescriptionId);
}
