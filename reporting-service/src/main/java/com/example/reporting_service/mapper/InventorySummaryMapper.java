package com.example.reporting_service.mapper;

import com.example.reporting_service.dto.InventorySummaryResponse;
import com.example.reporting_service.entity.InventorySummary;
import org.springframework.stereotype.Component;

@Component
public class InventorySummaryMapper {
    
    public InventorySummaryResponse toResponse(InventorySummary entity) {
        if (entity == null) {
            return null;
        }
        
        return InventorySummaryResponse.builder()
                .reportDate(entity.getReportDate())
                .branchId(entity.getBranchId())
                .branchName("Sucursal " + entity.getBranchId())
                .totalProducts(entity.getTotalProducts())
                .lowStockProducts(entity.getLowStockProducts())
                .expiringSoon(entity.getExpiringSoon())
                .inventoryValue(entity.getInventoryValue())
                .build();
    }
    
    public InventorySummary toEntity(InventorySummaryResponse response) {
        if (response == null) {
            return null;
        }
        
        InventorySummary entity = new InventorySummary();
        entity.setReportDate(response.getReportDate());
        entity.setBranchId(response.getBranchId());
        entity.setTotalProducts(response.getTotalProducts());
        entity.setLowStockProducts(response.getLowStockProducts());
        entity.setExpiringSoon(response.getExpiringSoon());
        entity.setInventoryValue(response.getInventoryValue());
        
        return entity;
    }
}
