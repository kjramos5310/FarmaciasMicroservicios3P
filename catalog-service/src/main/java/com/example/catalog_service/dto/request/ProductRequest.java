package com.example.catalog_service.dto.request;

import com.example.catalog_service.entity.enums.ProductStatus;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProductRequest {
    
    @NotBlank(message = "El código es obligatorio")
    @Size(max = 50, message = "El código no puede exceder 50 caracteres")
    private String code;
    
    @NotBlank(message = "El código de barras es obligatorio")
    @Size(max = 100, message = "El código de barras no puede exceder 100 caracteres")
    private String barcode;
    
    @NotBlank(message = "El nombre es obligatorio")
    @Size(max = 300, message = "El nombre no puede exceder 300 caracteres")
    private String name;
    
    @Size(max = 300, message = "El nombre genérico no puede exceder 300 caracteres")
    private String genericName;
    
    @Size(max = 1000, message = "La descripción no puede exceder 1000 caracteres")
    private String description;
    
    @Size(max = 200, message = "La presentación no puede exceder 200 caracteres")
    private String presentation;
    
    @NotNull(message = "La categoría es obligatoria")
    private Long categoryId;
    
    @NotNull(message = "El laboratorio es obligatorio")
    private Long laboratoryId;
    
    @NotNull(message = "El precio base es obligatorio")
    @DecimalMin(value = "0.0", inclusive = false, message = "El precio base debe ser mayor a 0")
    @Digits(integer = 8, fraction = 2, message = "El precio base debe tener máximo 8 dígitos enteros y 2 decimales")
    private BigDecimal basePrice;
    
    @NotNull(message = "El campo requiere receta es obligatorio")
    private Boolean requiresPrescription;
    
    @NotNull(message = "El campo es controlado es obligatorio")
    private Boolean isControlled;
    
    @Size(max = 500, message = "El ingrediente activo no puede exceder 500 caracteres")
    private String activeIngredient;
    
    @Size(max = 1000, message = "Las contraindicaciones no pueden exceder 1000 caracteres")
    private String contraindications;
    
    @Size(max = 300, message = "La dosificación no puede exceder 300 caracteres")
    private String dosage;
    
    @NotNull(message = "El estado es obligatorio")
    private ProductStatus status;
}
