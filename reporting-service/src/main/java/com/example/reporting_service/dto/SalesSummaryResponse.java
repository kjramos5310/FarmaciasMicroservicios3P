package com.example.reporting_service.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SalesSummaryResponse {
    private LocalDate reportDate;
    private Long branchId;
    private String branchName;
    private Integer totalSales;
    private BigDecimal totalRevenue;
    private BigDecimal averageTicket;
    private Integer totalItems;
    private Integer uniqueCustomers;
}
