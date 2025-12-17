package com.example.reporting_service.repository;

import com.example.reporting_service.entity.InventorySummary;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface InventorySummaryRepository extends JpaRepository<InventorySummary, Long> {
    
    @Query("SELECT i FROM InventorySummary i WHERE i.reportDate = :reportDate")
    List<InventorySummary> findByReportDate(@Param("reportDate") LocalDate reportDate);
    
    @Query("SELECT i FROM InventorySummary i WHERE i.reportDate = :reportDate " +
           "AND i.branchId = :branchId")
    Optional<InventorySummary> findByReportDateAndBranch(@Param("reportDate") LocalDate reportDate, 
                                                           @Param("branchId") Long branchId);
    
    @Query("SELECT COALESCE(SUM(i.totalProducts), 0) FROM InventorySummary i " +
           "WHERE i.reportDate = :reportDate")
    Integer calculateTotalProducts(@Param("reportDate") LocalDate reportDate);
    
    @Query("SELECT COALESCE(SUM(i.lowStockProducts), 0) FROM InventorySummary i " +
           "WHERE i.reportDate = :reportDate")
    Integer calculateLowStockProducts(@Param("reportDate") LocalDate reportDate);
    
    @Query("SELECT COALESCE(SUM(i.expiringSoon), 0) FROM InventorySummary i " +
           "WHERE i.reportDate = :reportDate")
    Integer calculateExpiringSoon(@Param("reportDate") LocalDate reportDate);
    
    @Query("SELECT COALESCE(SUM(i.inventoryValue), 0) FROM InventorySummary i " +
           "WHERE i.reportDate = :reportDate")
    BigDecimal calculateTotalInventoryValue(@Param("reportDate") LocalDate reportDate);
    
    Optional<InventorySummary> findByReportDateAndBranchId(LocalDate reportDate, Long branchId);
}
