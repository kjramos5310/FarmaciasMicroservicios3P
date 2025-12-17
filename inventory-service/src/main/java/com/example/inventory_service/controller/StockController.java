package com.example.inventory_service.controller;

import com.example.inventory_service.dto.request.StockRequest;
import com.example.inventory_service.dto.response.ApiResponse;
import com.example.inventory_service.dto.response.StockResponse;
import com.example.inventory_service.service.StockService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/stock")
@CrossOrigin
@RequiredArgsConstructor
public class StockController {
    
    private final StockService stockService;
    
    @GetMapping
    public ResponseEntity<ApiResponse<List<StockResponse>>> getAllStock() {
        List<StockResponse> stock = stockService.findAll();
        return ResponseEntity.ok(ApiResponse.success("Stock obtenido exitosamente", stock));
    }
    
    @GetMapping("/{branchId}")
    public ResponseEntity<ApiResponse<List<StockResponse>>> getStockByBranch(@PathVariable Long branchId) {
        List<StockResponse> stock = stockService.findByBranch(branchId);
        return ResponseEntity.ok(ApiResponse.success("Stock de sucursal obtenido exitosamente", stock));
    }
    
    @GetMapping("/{branchId}/{productId}")
    public ResponseEntity<ApiResponse<StockResponse>> getStockByBranchAndProduct(
            @PathVariable Long branchId,
            @PathVariable Long productId) {
        StockResponse stock = stockService.findByBranchAndProduct(branchId, productId);
        return ResponseEntity.ok(ApiResponse.success("Stock encontrado", stock));
    }
    
    @PostMapping
    public ResponseEntity<ApiResponse<StockResponse>> createOrUpdateStock(@Valid @RequestBody StockRequest request) {
        StockResponse stock = stockService.createOrUpdate(request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("Stock creado/actualizado exitosamente", stock));
    }
    
    @GetMapping("/alerts")
    public ResponseEntity<ApiResponse<List<StockResponse>>> getLowStockAlerts() {
        List<StockResponse> alerts = stockService.findLowStockAlerts();
        return ResponseEntity.ok(ApiResponse.success("Alertas de stock bajo obtenidas exitosamente", alerts));
    }
}
