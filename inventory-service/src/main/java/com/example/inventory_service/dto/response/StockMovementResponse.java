package com.example.inventory_service.dto.response;

import com.example.inventory_service.entity.enums.MovementType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class StockMovementResponse {
    
    private Long id;
    private Long branchId;
    private String branchName;
    private Long productId;
    private MovementType type;
    private Integer quantity;
    private String reason;
    private String reference;
    private Long destinationBranchId;
    private String destinationBranchName;
    private String performedBy;
    private LocalDateTime createdAt;
}
