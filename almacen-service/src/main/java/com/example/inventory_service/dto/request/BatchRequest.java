package com.example.inventory_service.dto.request;

import com.example.inventory_service.entity.enums.BatchStatus;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class BatchRequest {
    
    @NotBlank(message = "El número de lote es obligatorio")
    @Size(max = 100, message = "El número de lote no puede exceder 100 caracteres")
    private String batchNumber;
    
    @NotNull(message = "El ID del producto es obligatorio")
    private Long productId;
    
    @NotNull(message = "El ID de la sucursal es obligatorio")
    private Long branchId;
    
    @NotNull(message = "La cantidad es obligatoria")
    @Positive(message = "La cantidad debe ser mayor a cero")
    private Integer quantity;
    
    @Future(message = "La fecha de expiración debe ser futura")
    private LocalDate expirationDate;
    
    @PastOrPresent(message = "La fecha de fabricación debe ser pasada o presente")
    private LocalDate manufactureDate;
    
    @NotNull(message = "El estado es obligatorio")
    private BatchStatus status;
}
