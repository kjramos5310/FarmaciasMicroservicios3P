package com.example.inventory_service.mapper;

import com.example.inventory_service.dto.request.BranchRequest;
import com.example.inventory_service.dto.response.BranchResponse;
import com.example.inventory_service.entity.Branch;
import org.springframework.stereotype.Component;

@Component
public class BranchMapper {
    
    public Branch toEntity(BranchRequest request) {
        if (request == null) {
            return null;
        }
        
        Branch branch = new Branch();
        branch.setCode(request.getCode());
        branch.setName(request.getName());
        branch.setAddress(request.getAddress());
        branch.setCity(request.getCity());
        branch.setProvince(request.getProvince());
        branch.setPhone(request.getPhone());
        branch.setEmail(request.getEmail());
        branch.setManagerName(request.getManagerName());
        branch.setStatus(request.getStatus());
        branch.setOpeningTime(request.getOpeningTime());
        branch.setClosingTime(request.getClosingTime());
        
        return branch;
    }
    
    public BranchResponse toResponse(Branch branch) {
        if (branch == null) {
            return null;
        }
        
        BranchResponse response = new BranchResponse();
        response.setId(branch.getId());
        response.setCode(branch.getCode());
        response.setName(branch.getName());
        response.setAddress(branch.getAddress());
        response.setCity(branch.getCity());
        response.setProvince(branch.getProvince());
        response.setPhone(branch.getPhone());
        response.setEmail(branch.getEmail());
        response.setManagerName(branch.getManagerName());
        response.setStatus(branch.getStatus());
        response.setOpeningTime(branch.getOpeningTime());
        response.setClosingTime(branch.getClosingTime());
        response.setCreatedAt(branch.getCreatedAt());
        
        return response;
    }
    
    public void updateEntity(Branch branch, BranchRequest request) {
        if (branch == null || request == null) {
            return;
        }
        
        branch.setCode(request.getCode());
        branch.setName(request.getName());
        branch.setAddress(request.getAddress());
        branch.setCity(request.getCity());
        branch.setProvince(request.getProvince());
        branch.setPhone(request.getPhone());
        branch.setEmail(request.getEmail());
        branch.setManagerName(request.getManagerName());
        branch.setStatus(request.getStatus());
        branch.setOpeningTime(request.getOpeningTime());
        branch.setClosingTime(request.getClosingTime());
    }
}
