package com.example.sales_service.dto;

import com.example.sales_service.enums.PaymentMethod;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CreateSaleRequest {
    
    private Long customerId;
    
    @NotNull(message = "El ID de la sucursal es obligatorio")
    private Long branchId;
    
    @NotEmpty(message = "La venta debe tener al menos un ítem")
    @Valid
    private List<SaleItemRequest> items;
    
    private BigDecimal discount;
    
    @NotNull(message = "El método de pago es obligatorio")
    private PaymentMethod paymentMethod;
    
    @NotNull(message = "El nombre del cajero es obligatorio")
    private String cashierName;
    
    private String notes;
}
