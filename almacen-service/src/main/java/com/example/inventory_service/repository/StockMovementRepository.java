package com.example.inventory_service.repository;

import com.example.inventory_service.entity.StockMovement;
import com.example.inventory_service.entity.enums.MovementType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface StockMovementRepository extends JpaRepository<StockMovement, Long> {
    
    List<StockMovement> findByBranchId(Long branchId);
    
    List<StockMovement> findByProductId(Long productId);
    
    List<StockMovement> findByType(MovementType type);
    
    List<StockMovement> findByBranchIdAndProductId(Long branchId, Long productId);
}
