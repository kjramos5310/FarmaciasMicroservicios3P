package com.example.sales_service.repository;

import com.example.sales_service.entity.Prescription;
import com.example.sales_service.enums.PrescriptionStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PrescriptionRepository extends JpaRepository<Prescription, Long> {
    Optional<Prescription> findByPrescriptionNumber(String prescriptionNumber);
    Page<Prescription> findByCustomerId(Long customerId, Pageable pageable);
    List<Prescription> findByCustomerIdAndStatus(Long customerId, PrescriptionStatus status);
    boolean existsByPrescriptionNumber(String prescriptionNumber);
}
