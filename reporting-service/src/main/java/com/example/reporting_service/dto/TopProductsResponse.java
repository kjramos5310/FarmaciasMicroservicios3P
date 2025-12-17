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
public class TopProductsResponse {
    private Long productId;
    private String productName;
    private String productCode;
    private Integer quantitySold;
    private BigDecimal revenue;
    private Integer rank;
}
