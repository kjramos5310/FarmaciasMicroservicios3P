package com.example.inventory_service.dto.request;

import com.example.inventory_service.entity.enums.MovementType;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class StockMovementRequest {
    
    @NotNull(message = "El ID de la sucursal es obligatorio")
    private Long branchId;
    
    @NotNull(message = "El ID del producto es obligatorio")
    private Long productId;
    
    @NotNull(message = "El tipo de movimiento es obligatorio")
    private MovementType type;
    
    @NotNull(message = "La cantidad es obligatoria")
    @Positive(message = "La cantidad debe ser mayor a cero")
    private Integer quantity;
    
    @Size(max = 500, message = "La razón no puede exceder 500 caracteres")
    private String reason;
    
    @Size(max = 100, message = "La referencia no puede exceder 100 caracteres")
    private String reference;
    
    private Long destinationBranchId;
    
    @Size(max = 150, message = "El nombre de quien realiza no puede exceder 150 caracteres")
    private String performedBy;
}
