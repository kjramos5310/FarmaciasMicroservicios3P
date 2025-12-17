package com.example.inventory_service.repository;

import com.example.inventory_service.entity.Stock;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface StockRepository extends JpaRepository<Stock, Long> {
    
    Optional<Stock> findByBranchIdAndProductId(Long branchId, Long productId);
    
    List<Stock> findByBranchId(Long branchId);
    
    List<Stock> findByProductId(Long productId);
    
    @Query("SELECT s FROM Stock s WHERE s.branch.id = :branchId AND s.quantity < s.minimumStock")
    List<Stock> findByBranchAndQuantityLessThan(@Param("branchId") Long branchId);
    
    @Query("SELECT s FROM Stock s WHERE s.quantity < s.minimumStock")
    List<Stock> findAllBelowMinimum();
}
