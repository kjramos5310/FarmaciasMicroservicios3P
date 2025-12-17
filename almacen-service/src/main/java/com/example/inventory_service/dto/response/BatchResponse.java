package com.example.inventory_service.dto.response;

import com.example.inventory_service.entity.enums.BatchStatus;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class BatchResponse {
    
    private Long id;
    private String batchNumber;
    private Long productId;
    private Long branchId;
    private String branchName;
    private Integer quantity;
    private LocalDate expirationDate;
    private LocalDate manufactureDate;
    private BatchStatus status;
    private LocalDateTime createdAt;
    private Boolean expiringSoon;
}
