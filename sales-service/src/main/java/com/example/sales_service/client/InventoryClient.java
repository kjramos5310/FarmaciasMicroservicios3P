package com.example.sales_service.client;

import com.example.sales_service.dto.StockDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(name = "inventory-service", url = "${services.inventory.url}")
public interface InventoryClient {
    
    @GetMapping("/api/inventory/stock")
    StockDTO getStock(@RequestParam("branchId") Long branchId, @RequestParam("productId") Long productId);
}
