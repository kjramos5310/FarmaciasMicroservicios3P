package com.example.reporting_service.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DashboardResponse {
    private SalesMetrics salesMetrics;
    private InventoryMetrics inventoryMetrics;
    
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class SalesMetrics {
        private BigDecimal totalRevenue;
        private Integer totalSales;
        private BigDecimal averageTicket;
        private Integer uniqueCustomers;
    }
    
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class InventoryMetrics {
        private Integer totalProducts;
        private Integer lowStockProducts;
        private Integer expiringSoon;
        private BigDecimal totalInventoryValue;
    }
}
