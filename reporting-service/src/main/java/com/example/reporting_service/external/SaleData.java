package com.example.reporting_service.external;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SaleData {
    private Long id;
    private LocalDate saleDate;
    private Long branchId;
    private Long customerId;
    private BigDecimal totalAmount;
    private Integer itemCount;
    private String status;
}
