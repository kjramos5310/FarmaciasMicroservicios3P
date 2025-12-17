package com.example.sales_service.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SaleItemRequest {
    
    @NotNull(message = "El ID del producto es obligatorio")
    private Long productId;
    
    @NotNull(message = "La cantidad es obligatoria")
    @Positive(message = "La cantidad debe ser mayor a cero")
    private Integer quantity;
    
    private BigDecimal discount;
    
    private Long batchId;
    
    private Boolean requiresPrescription;
    
    private Long prescriptionId;
}
