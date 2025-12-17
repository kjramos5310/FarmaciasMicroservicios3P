package com.example.reporting_service.mapper;

import com.example.reporting_service.dto.SalesSummaryResponse;
import com.example.reporting_service.entity.SalesSummary;
import org.springframework.stereotype.Component;

@Component
public class SalesSummaryMapper {
    
    public SalesSummaryResponse toResponse(SalesSummary entity) {
        if (entity == null) {
            return null;
        }
        
        return SalesSummaryResponse.builder()
                .reportDate(entity.getReportDate())
                .branchId(entity.getBranchId())
                .branchName("Sucursal " + entity.getBranchId())
                .totalSales(entity.getTotalSales())
                .totalRevenue(entity.getTotalRevenue())
                .averageTicket(entity.getAverageTicket())
                .totalItems(entity.getTotalItems())
                .uniqueCustomers(entity.getUniqueCustomers())
                .build();
    }
    
    public SalesSummary toEntity(SalesSummaryResponse response) {
        if (response == null) {
            return null;
        }
        
        SalesSummary entity = new SalesSummary();
        entity.setReportDate(response.getReportDate());
        entity.setBranchId(response.getBranchId());
        entity.setTotalSales(response.getTotalSales());
        entity.setTotalRevenue(response.getTotalRevenue());
        entity.setAverageTicket(response.getAverageTicket());
        entity.setTotalItems(response.getTotalItems());
        entity.setUniqueCustomers(response.getUniqueCustomers());
        
        return entity;
    }
}
