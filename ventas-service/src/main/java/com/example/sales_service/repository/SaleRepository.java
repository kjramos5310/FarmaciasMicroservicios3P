package com.example.sales_service.repository;

import com.example.sales_service.entity.Sale;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface SaleRepository extends JpaRepository<Sale, Long> {
    Optional<Sale> findBySaleNumber(String saleNumber);
    Page<Sale> findByCustomerId(Long customerId, Pageable pageable);
    Page<Sale> findByBranchId(Long branchId, Pageable pageable);
    List<Sale> findByBranchIdAndSaleDateBetween(Long branchId, LocalDateTime start, LocalDateTime end);
    Page<Sale> findBySaleDateBetween(LocalDateTime start, LocalDateTime end, Pageable pageable);
    Long countBySaleDateBetween(LocalDateTime start, LocalDateTime end);
}
