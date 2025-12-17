package com.example.reporting_service.repository;

import com.example.reporting_service.entity.SalesSummary;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface SalesSummaryRepository extends JpaRepository<SalesSummary, Long> {
    
    @Query("SELECT s FROM SalesSummary s WHERE s.reportDate BETWEEN :startDate AND :endDate")
    List<SalesSummary> findByDateRange(@Param("startDate") LocalDate startDate, 
                                        @Param("endDate") LocalDate endDate);
    
    @Query("SELECT s FROM SalesSummary s WHERE s.reportDate BETWEEN :startDate AND :endDate " +
           "AND s.branchId = :branchId")
    List<SalesSummary> findByDateRangeAndBranch(@Param("startDate") LocalDate startDate, 
                                                  @Param("endDate") LocalDate endDate,
                                                  @Param("branchId") Long branchId);
    
    @Query("SELECT COALESCE(SUM(s.totalRevenue), 0) FROM SalesSummary s " +
           "WHERE s.reportDate BETWEEN :startDate AND :endDate")
    BigDecimal calculateTotalRevenue(@Param("startDate") LocalDate startDate, 
                                      @Param("endDate") LocalDate endDate);
    
    @Query("SELECT COALESCE(SUM(s.totalSales), 0) FROM SalesSummary s " +
           "WHERE s.reportDate BETWEEN :startDate AND :endDate")
    Integer calculateTotalSales(@Param("startDate") LocalDate startDate, 
                                @Param("endDate") LocalDate endDate);
    
    @Query("SELECT COALESCE(AVG(s.averageTicket), 0) FROM SalesSummary s " +
           "WHERE s.reportDate BETWEEN :startDate AND :endDate")
    BigDecimal calculateAverageTicket(@Param("startDate") LocalDate startDate, 
                                       @Param("endDate") LocalDate endDate);
    
    Optional<SalesSummary> findByReportDateAndBranchId(LocalDate reportDate, Long branchId);
}
