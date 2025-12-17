package com.example.reporting_service.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ExpiringProductResponse {
    private Long productId;
    private String productName;
    private Long branchId;
    private LocalDate expiryDate;
    private Integer daysUntilExpiry;
    private Integer quantity;
}
