package com.example.inventory_service.controller;

import com.example.inventory_service.dto.request.StockMovementRequest;
import com.example.inventory_service.dto.response.ApiResponse;
import com.example.inventory_service.dto.response.StockMovementResponse;
import com.example.inventory_service.service.StockMovementService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/movements")
@CrossOrigin
@RequiredArgsConstructor
public class StockMovementController {
    
    private final StockMovementService movementService;
    
    @PostMapping
    public ResponseEntity<ApiResponse<StockMovementResponse>> createMovement(
            @Valid @RequestBody StockMovementRequest request) {
        StockMovementResponse movement = movementService.create(request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("Movimiento registrado exitosamente", movement));
    }
    
    @GetMapping("/branch/{branchId}")
    public ResponseEntity<ApiResponse<List<StockMovementResponse>>> getMovementsByBranch(@PathVariable Long branchId) {
        List<StockMovementResponse> movements = movementService.findByBranch(branchId);
        return ResponseEntity.ok(ApiResponse.success("Movimientos de sucursal obtenidos exitosamente", movements));
    }
    
    @GetMapping("/product/{productId}")
    public ResponseEntity<ApiResponse<List<StockMovementResponse>>> getMovementsByProduct(@PathVariable Long productId) {
        List<StockMovementResponse> movements = movementService.findByProduct(productId);
        return ResponseEntity.ok(ApiResponse.success("Movimientos de producto obtenidos exitosamente", movements));
    }
}
