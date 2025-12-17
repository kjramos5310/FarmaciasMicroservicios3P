package com.example.inventory_service.controller;

import com.example.inventory_service.dto.request.BranchRequest;
import com.example.inventory_service.dto.response.ApiResponse;
import com.example.inventory_service.dto.response.BranchResponse;
import com.example.inventory_service.service.BranchService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/branches")
@CrossOrigin
@RequiredArgsConstructor
public class BranchController {
    
    private final BranchService branchService;
    
    @GetMapping
    public ResponseEntity<ApiResponse<List<BranchResponse>>> getAllBranches() {
        List<BranchResponse> branches = branchService.findAll();
        return ResponseEntity.ok(ApiResponse.success("Sucursales obtenidas exitosamente", branches));
    }
    
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<BranchResponse>> getBranchById(@PathVariable Long id) {
        BranchResponse branch = branchService.findById(id);
        return ResponseEntity.ok(ApiResponse.success("Sucursal encontrada", branch));
    }
    
    @PostMapping
    public ResponseEntity<ApiResponse<BranchResponse>> createBranch(@Valid @RequestBody BranchRequest request) {
        BranchResponse branch = branchService.create(request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("Sucursal creada exitosamente", branch));
    }
    
    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<BranchResponse>> updateBranch(
            @PathVariable Long id,
            @Valid @RequestBody BranchRequest request) {
        BranchResponse branch = branchService.update(id, request);
        return ResponseEntity.ok(ApiResponse.success("Sucursal actualizada exitosamente", branch));
    }
    
    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteBranch(@PathVariable Long id) {
        branchService.delete(id);
        return ResponseEntity.ok(ApiResponse.success("Sucursal eliminada exitosamente", null));
    }
}
