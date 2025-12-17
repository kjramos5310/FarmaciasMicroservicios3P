package com.example.inventory_service.repository;

import com.example.inventory_service.entity.Batch;
import com.example.inventory_service.entity.enums.BatchStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface BatchRepository extends JpaRepository<Batch, Long> {
    
    List<Batch> findByBranchId(Long branchId);
    
    List<Batch> findByProductId(Long productId);
    
    List<Batch> findByStatus(BatchStatus status);
    
    @Query("SELECT b FROM Batch b WHERE b.expirationDate <= :date AND b.status = 'AVAILABLE'")
    List<Batch> findByExpirationDateBefore(@Param("date") LocalDate date);
    
    @Query("SELECT b FROM Batch b WHERE b.expirationDate BETWEEN :startDate AND :endDate AND b.status = 'AVAILABLE'")
    List<Batch> findExpiringSoon(@Param("startDate") LocalDate startDate, @Param("endDate") LocalDate endDate);
}
