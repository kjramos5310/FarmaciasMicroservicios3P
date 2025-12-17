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
public class BranchSalesResponse {
    private Long branchId;
    private String branchName;
    private Integer totalSales;
    private BigDecimal totalRevenue;
    private BigDecimal averageTicket;
    private Integer totalItems;
}
