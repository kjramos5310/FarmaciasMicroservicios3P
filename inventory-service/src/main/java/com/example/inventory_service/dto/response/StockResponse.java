package com.example.inventory_service.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class StockResponse {
    
    private Long id;
    private Long branchId;
    private String branchName;
    private Long productId;
    private Integer quantity;
    private Integer minimumStock;
    private Integer maximumStock;
    private LocalDateTime lastRestockDate;
    private LocalDateTime updatedAt;
    private Boolean belowMinimum;
}
