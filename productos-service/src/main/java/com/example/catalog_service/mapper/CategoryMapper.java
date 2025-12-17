package com.example.catalog_service.mapper;

import com.example.catalog_service.dto.request.CategoryRequest;
import com.example.catalog_service.dto.response.CategoryResponse;
import com.example.catalog_service.entity.Category;
import org.springframework.stereotype.Component;

@Component
public class CategoryMapper {
    
    public Category toEntity(CategoryRequest request) {
        if (request == null) {
            return null;
        }
        
        Category category = new Category();
        category.setCode(request.getCode());
        category.setName(request.getName());
        category.setDescription(request.getDescription());
        category.setIsActive(request.getIsActive());
        return category;
    }
    
    public CategoryResponse toResponse(Category category) {
        if (category == null) {
            return null;
        }
        
        CategoryResponse response = new CategoryResponse();
        response.setId(category.getId());
        response.setCode(category.getCode());
        response.setName(category.getName());
        response.setDescription(category.getDescription());
        response.setIsActive(category.getIsActive());
        response.setCreatedAt(category.getCreatedAt());
        
        // Mapear categoría padre si existe
        if (category.getParentCategory() != null) {
            CategoryResponse parentResponse = new CategoryResponse();
            parentResponse.setId(category.getParentCategory().getId());
            parentResponse.setCode(category.getParentCategory().getCode());
            parentResponse.setName(category.getParentCategory().getName());
            parentResponse.setDescription(category.getParentCategory().getDescription());
            parentResponse.setIsActive(category.getParentCategory().getIsActive());
            parentResponse.setCreatedAt(category.getParentCategory().getCreatedAt());
            response.setParentCategory(parentResponse);
        }
        
        return response;
    }
    
    public void updateEntity(Category category, CategoryRequest request) {
        if (category == null || request == null) {
            return;
        }
        
        category.setCode(request.getCode());
        category.setName(request.getName());
        category.setDescription(request.getDescription());
        category.setIsActive(request.getIsActive());
    }
}
