package com.example.inventory_service.controller;

import com.example.inventory_service.dto.request.BatchRequest;
import com.example.inventory_service.dto.response.ApiResponse;
import com.example.inventory_service.dto.response.BatchResponse;
import com.example.inventory_service.service.BatchService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/batches")
@CrossOrigin
@RequiredArgsConstructor
public class BatchController {
    
    private final BatchService batchService;
    
    @PostMapping
    public ResponseEntity<ApiResponse<BatchResponse>> createBatch(@Valid @RequestBody BatchRequest request) {
        BatchResponse batch = batchService.create(request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("Lote creado exitosamente", batch));
    }
    
    @GetMapping
    public ResponseEntity<ApiResponse<List<BatchResponse>>> getAllBatches() {
        List<BatchResponse> batches = batchService.findAll();
        return ResponseEntity.ok(ApiResponse.success("Lotes obtenidos exitosamente", batches));
    }
    
    @GetMapping("/branch/{branchId}")
    public ResponseEntity<ApiResponse<List<BatchResponse>>> getBatchesByBranch(@PathVariable Long branchId) {
        List<BatchResponse> batches = batchService.findByBranch(branchId);
        return ResponseEntity.ok(ApiResponse.success("Lotes de sucursal obtenidos exitosamente", batches));
    }
    
    @GetMapping("/expiring")
    public ResponseEntity<ApiResponse<List<BatchResponse>>> getExpiringBatches() {
        List<BatchResponse> batches = batchService.findExpiringSoon();
        return ResponseEntity.ok(ApiResponse.success("Lotes por vencer obtenidos exitosamente", batches));
    }
}
