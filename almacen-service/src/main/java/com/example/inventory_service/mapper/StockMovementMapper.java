package com.example.inventory_service.mapper;

import com.example.inventory_service.dto.response.StockMovementResponse;
import com.example.inventory_service.entity.StockMovement;
import org.springframework.stereotype.Component;

@Component
public class StockMovementMapper {
    
    public StockMovementResponse toResponse(StockMovement movement) {
        if (movement == null) {
            return null;
        }
        
        StockMovementResponse response = new StockMovementResponse();
        response.setId(movement.getId());
        response.setBranchId(movement.getBranch().getId());
        response.setBranchName(movement.getBranch().getName());
        response.setProductId(movement.getProductId());
        response.setType(movement.getType());
        response.setQuantity(movement.getQuantity());
        response.setReason(movement.getReason());
        response.setReference(movement.getReference());
        
        if (movement.getDestinationBranch() != null) {
            response.setDestinationBranchId(movement.getDestinationBranch().getId());
            response.setDestinationBranchName(movement.getDestinationBranch().getName());
        }
        
        response.setPerformedBy(movement.getPerformedBy());
        response.setCreatedAt(movement.getCreatedAt());
        
        return response;
    }
}
