package com.example.reporting_service.external;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class StockData {
    private Long id;
    private Long productId;
    private String productName;
    private String productCode;
    private Long branchId;
    private Integer quantity;
    private Integer minStock;
    private LocalDate expiryDate;
    private BigDecimal unitPrice;
}
