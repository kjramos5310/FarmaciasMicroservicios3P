package com.example.inventory_service.mapper;

import com.example.inventory_service.dto.request.BatchRequest;
import com.example.inventory_service.dto.response.BatchResponse;
import com.example.inventory_service.entity.Batch;
import org.springframework.stereotype.Component;

import java.time.LocalDate;

@Component
public class BatchMapper {
    
    public BatchResponse toResponse(Batch batch) {
        if (batch == null) {
            return null;
        }
        
        BatchResponse response = new BatchResponse();
        response.setId(batch.getId());
        response.setBatchNumber(batch.getBatchNumber());
        response.setProductId(batch.getProductId());
        response.setBranchId(batch.getBranch().getId());
        response.setBranchName(batch.getBranch().getName());
        response.setQuantity(batch.getQuantity());
        response.setExpirationDate(batch.getExpirationDate());
        response.setManufactureDate(batch.getManufactureDate());
        response.setStatus(batch.getStatus());
        response.setCreatedAt(batch.getCreatedAt());
        
        // Check if expiring within 30 days
        if (batch.getExpirationDate() != null) {
            LocalDate thirtyDaysFromNow = LocalDate.now().plusDays(30);
            response.setExpiringSoon(batch.getExpirationDate().isBefore(thirtyDaysFromNow));
        } else {
            response.setExpiringSoon(false);
        }
        
        return response;
    }
}
