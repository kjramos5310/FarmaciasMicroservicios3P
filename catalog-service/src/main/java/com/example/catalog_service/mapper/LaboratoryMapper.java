package com.example.catalog_service.mapper;

import com.example.catalog_service.dto.request.LaboratoryRequest;
import com.example.catalog_service.dto.response.LaboratoryResponse;
import com.example.catalog_service.entity.Laboratory;
import org.springframework.stereotype.Component;

@Component
public class LaboratoryMapper {
    
    public Laboratory toEntity(LaboratoryRequest request) {
        if (request == null) {
            return null;
        }
        
        Laboratory laboratory = new Laboratory();
        laboratory.setName(request.getName());
        laboratory.setCountry(request.getCountry());
        laboratory.setContactEmail(request.getContactEmail());
        laboratory.setPhone(request.getPhone());
        laboratory.setWebsite(request.getWebsite());
        laboratory.setIsActive(request.getIsActive());
        return laboratory;
    }
    
    public LaboratoryResponse toResponse(Laboratory laboratory) {
        if (laboratory == null) {
            return null;
        }
        
        LaboratoryResponse response = new LaboratoryResponse();
        response.setId(laboratory.getId());
        response.setName(laboratory.getName());
        response.setCountry(laboratory.getCountry());
        response.setContactEmail(laboratory.getContactEmail());
        response.setPhone(laboratory.getPhone());
        response.setWebsite(laboratory.getWebsite());
        response.setIsActive(laboratory.getIsActive());
        response.setCreatedAt(laboratory.getCreatedAt());
        return response;
    }
    
    public void updateEntity(Laboratory laboratory, LaboratoryRequest request) {
        if (laboratory == null || request == null) {
            return;
        }
        
        laboratory.setName(request.getName());
        laboratory.setCountry(request.getCountry());
        laboratory.setContactEmail(request.getContactEmail());
        laboratory.setPhone(request.getPhone());
        laboratory.setWebsite(request.getWebsite());
        laboratory.setIsActive(request.getIsActive());
    }
}
