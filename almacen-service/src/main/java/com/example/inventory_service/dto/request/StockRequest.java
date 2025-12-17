package com.example.inventory_service.dto.request;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class StockRequest {
    
    @NotNull(message = "El ID de la sucursal es obligatorio")
    private Long branchId;
    
    @NotNull(message = "El ID del producto es obligatorio")
    private Long productId;
    
    @NotNull(message = "La cantidad es obligatoria")
    @PositiveOrZero(message = "La cantidad debe ser mayor o igual a cero")
    private Integer quantity;
    
    @NotNull(message = "El stock mínimo es obligatorio")
    @PositiveOrZero(message = "El stock mínimo debe ser mayor o igual a cero")
    private Integer minimumStock;
    
    @NotNull(message = "El stock máximo es obligatorio")
    @Positive(message = "El stock máximo debe ser mayor a cero")
    private Integer maximumStock;
}
