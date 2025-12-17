package com.example.catalog_service.mapper;

import com.example.catalog_service.dto.request.ProductRequest;
import com.example.catalog_service.dto.response.CategoryResponse;
import com.example.catalog_service.dto.response.LaboratoryResponse;
import com.example.catalog_service.dto.response.ProductResponse;
import com.example.catalog_service.entity.Product;
import org.springframework.stereotype.Component;

@Component
public class ProductMapper {
    
    private final CategoryMapper categoryMapper;
    private final LaboratoryMapper laboratoryMapper;
    
    public ProductMapper(CategoryMapper categoryMapper, LaboratoryMapper laboratoryMapper) {
        this.categoryMapper = categoryMapper;
        this.laboratoryMapper = laboratoryMapper;
    }
    
    public Product toEntity(ProductRequest request) {
        if (request == null) {
            return null;
        }
        
        Product product = new Product();
        product.setCode(request.getCode());
        product.setBarcode(request.getBarcode());
        product.setName(request.getName());
        product.setGenericName(request.getGenericName());
        product.setDescription(request.getDescription());
        product.setPresentation(request.getPresentation());
        product.setBasePrice(request.getBasePrice());
        product.setRequiresPrescription(request.getRequiresPrescription());
        product.setIsControlled(request.getIsControlled());
        product.setActiveIngredient(request.getActiveIngredient());
        product.setContraindications(request.getContraindications());
        product.setDosage(request.getDosage());
        product.setStatus(request.getStatus());
        return product;
    }
    
    public ProductResponse toResponse(Product product) {
        if (product == null) {
            return null;
        }
        
        ProductResponse response = new ProductResponse();
        response.setId(product.getId());
        response.setCode(product.getCode());
        response.setBarcode(product.getBarcode());
        response.setName(product.getName());
        response.setGenericName(product.getGenericName());
        response.setDescription(product.getDescription());
        response.setPresentation(product.getPresentation());
        response.setBasePrice(product.getBasePrice());
        response.setRequiresPrescription(product.getRequiresPrescription());
        response.setIsControlled(product.getIsControlled());
        response.setActiveIngredient(product.getActiveIngredient());
        response.setContraindications(product.getContraindications());
        response.setDosage(product.getDosage());
        response.setStatus(product.getStatus());
        response.setCreatedAt(product.getCreatedAt());
        response.setUpdatedAt(product.getUpdatedAt());
        
        // Desnormalización: incluir datos completos de category y laboratory
        if (product.getCategory() != null) {
            CategoryResponse categoryResponse = categoryMapper.toResponse(product.getCategory());
            response.setCategory(categoryResponse);
        }
        
        if (product.getLaboratory() != null) {
            LaboratoryResponse laboratoryResponse = laboratoryMapper.toResponse(product.getLaboratory());
            response.setLaboratory(laboratoryResponse);
        }
        
        return response;
    }
    
    public void updateEntity(Product product, ProductRequest request) {
        if (product == null || request == null) {
            return;
        }
        
        product.setCode(request.getCode());
        product.setBarcode(request.getBarcode());
        product.setName(request.getName());
        product.setGenericName(request.getGenericName());
        product.setDescription(request.getDescription());
        product.setPresentation(request.getPresentation());
        product.setBasePrice(request.getBasePrice());
        product.setRequiresPrescription(request.getRequiresPrescription());
        product.setIsControlled(request.getIsControlled());
        product.setActiveIngredient(request.getActiveIngredient());
        product.setContraindications(request.getContraindications());
        product.setDosage(request.getDosage());
        product.setStatus(request.getStatus());
    }
}
