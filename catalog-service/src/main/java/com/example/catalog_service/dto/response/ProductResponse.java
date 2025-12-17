package com.example.catalog_service.dto.response;

import com.example.catalog_service.entity.enums.ProductStatus;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProductResponse {
    private Long id;
    private String code;
    private String barcode;
    private String name;
    private String genericName;
    private String description;
    private String presentation;
    private CategoryResponse category;
    private LaboratoryResponse laboratory;
    private BigDecimal basePrice;
    private Boolean requiresPrescription;
    private Boolean isControlled;
    private String activeIngredient;
    private String contraindications;
    private String dosage;
    private ProductStatus status;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
