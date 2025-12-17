package com.example.inventory_service.mapper;

import com.example.inventory_service.dto.request.StockRequest;
import com.example.inventory_service.dto.response.StockResponse;
import com.example.inventory_service.entity.Stock;
import org.springframework.stereotype.Component;

@Component
public class StockMapper {
    
    public StockResponse toResponse(Stock stock) {
        if (stock == null) {
            return null;
        }
        
        StockResponse response = new StockResponse();
        response.setId(stock.getId());
        response.setBranchId(stock.getBranch().getId());
        response.setBranchName(stock.getBranch().getName());
        response.setProductId(stock.getProductId());
        response.setQuantity(stock.getQuantity());
        response.setMinimumStock(stock.getMinimumStock());
        response.setMaximumStock(stock.getMaximumStock());
        response.setLastRestockDate(stock.getLastRestockDate());
        response.setUpdatedAt(stock.getUpdatedAt());
        response.setBelowMinimum(stock.getQuantity() < stock.getMinimumStock());
        
        return response;
    }
    
    public void updateFromRequest(Stock stock, StockRequest request) {
        if (stock == null || request == null) {
            return;
        }
        
        stock.setQuantity(request.getQuantity());
        stock.setMinimumStock(request.getMinimumStock());
        stock.setMaximumStock(request.getMaximumStock());
    }
}
