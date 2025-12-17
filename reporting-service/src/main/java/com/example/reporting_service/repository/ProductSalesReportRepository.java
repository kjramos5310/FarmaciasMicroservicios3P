package com.example.reporting_service.repository;

import com.example.reporting_service.entity.ProductSalesReport;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface ProductSalesReportRepository extends JpaRepository<ProductSalesReport, Long> {
    
    @Query("SELECT p FROM ProductSalesReport p WHERE p.reportDate BETWEEN :startDate AND :endDate " +
           "ORDER BY p.quantitySold DESC")
    List<ProductSalesReport> findByDateRange(@Param("startDate") LocalDate startDate, 
                                              @Param("endDate") LocalDate endDate);
    
    @Query("SELECT p FROM ProductSalesReport p WHERE p.reportDate BETWEEN :startDate AND :endDate " +
           "ORDER BY p.revenue DESC")
    List<ProductSalesReport> findTopByRevenue(@Param("startDate") LocalDate startDate, 
                                               @Param("endDate") LocalDate endDate);
    
    @Query("SELECT p FROM ProductSalesReport p WHERE p.reportDate BETWEEN :startDate AND :endDate " +
           "ORDER BY p.quantitySold DESC")
    List<ProductSalesReport> findTopByQuantity(@Param("startDate") LocalDate startDate, 
                                                @Param("endDate") LocalDate endDate);
    
    @Query("SELECT p FROM ProductSalesReport p WHERE p.reportDate BETWEEN :startDate AND :endDate " +
           "AND p.branchId = :branchId ORDER BY p.quantitySold DESC")
    List<ProductSalesReport> findByDateRangeAndBranch(@Param("startDate") LocalDate startDate, 
                                                       @Param("endDate") LocalDate endDate,
                                                       @Param("branchId") Long branchId);
    
    List<ProductSalesReport> findByProductId(Long productId);
}
