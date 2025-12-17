package com.example.reporting_service.controller;

import com.example.reporting_service.dto.*;
import com.example.reporting_service.service.ReportingService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/reports")
@RequiredArgsConstructor
@Slf4j
@CrossOrigin(origins = "*")
public class ReportController {
    
    private final ReportingService reportingService;
    
    // ========== Sales Reports ==========
    
    @GetMapping("/sales/summary")
    public ResponseEntity<SalesSummaryResponse> getSalesSummary(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate,
            @RequestParam(required = false) Long branchId) {
        
        log.info("GET /api/reports/sales/summary - startDate: {}, endDate: {}, branchId: {}", 
                startDate, endDate, branchId);
        
        SalesSummaryResponse response = reportingService.getSalesSummary(startDate, endDate, branchId);
        return ResponseEntity.ok(response);
    }
    
    @GetMapping("/sales/by-product")
    public ResponseEntity<List<ProductSalesResponse>> getSalesByProduct(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
        
        log.info("GET /api/reports/sales/by-product - startDate: {}, endDate: {}", startDate, endDate);
        
        List<ProductSalesResponse> response = reportingService.getSalesByProduct(startDate, endDate);
        return ResponseEntity.ok(response);
    }
    
    @GetMapping("/sales/top-products")
    public ResponseEntity<List<TopProductsResponse>> getTopProducts(
            @RequestParam(defaultValue = "10") int limit,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
        
        // Default to last 30 days if not specified
        if (startDate == null) {
            startDate = LocalDate.now().minusDays(30);
        }
        if (endDate == null) {
            endDate = LocalDate.now();
        }
        
        log.info("GET /api/reports/sales/top-products - limit: {}, startDate: {}, endDate: {}", 
                limit, startDate, endDate);
        
        List<TopProductsResponse> response = reportingService.getTopProducts(startDate, endDate, limit);
        return ResponseEntity.ok(response);
    }
    
    @GetMapping("/sales/by-branch")
    public ResponseEntity<List<BranchSalesResponse>> getSalesByBranch(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
        
        log.info("GET /api/reports/sales/by-branch - startDate: {}, endDate: {}", startDate, endDate);
        
        List<BranchSalesResponse> response = reportingService.getSalesByBranch(startDate, endDate);
        return ResponseEntity.ok(response);
    }
    
    // ========== Inventory Reports ==========
    
    @GetMapping("/inventory/summary")
    public ResponseEntity<InventorySummaryResponse> getInventorySummary(
            @RequestParam(required = false) Long branchId) {
        
        log.info("GET /api/reports/inventory/summary - branchId: {}", branchId);
        
        InventorySummaryResponse response = reportingService.getInventorySummary(branchId);
        return ResponseEntity.ok(response);
    }
    
    @GetMapping("/inventory/low-stock")
    public ResponseEntity<List<LowStockResponse>> getLowStockProducts(
            @RequestParam(required = false) Long branchId) {
        
        log.info("GET /api/reports/inventory/low-stock - branchId: {}", branchId);
        
        List<LowStockResponse> response = reportingService.getLowStockProducts(branchId);
        return ResponseEntity.ok(response);
    }
    
    @GetMapping("/inventory/expiring")
    public ResponseEntity<List<ExpiringProductResponse>> getExpiringProducts(
            @RequestParam(required = false) Long branchId) {
        
        log.info("GET /api/reports/inventory/expiring - branchId: {}", branchId);
        
        List<ExpiringProductResponse> response = reportingService.getExpiringProducts(branchId);
        return ResponseEntity.ok(response);
    }
    
    @GetMapping("/inventory/value")
    public ResponseEntity<BigDecimal> getInventoryValue(
            @RequestParam(required = false) Long branchId) {
        
        log.info("GET /api/reports/inventory/value - branchId: {}", branchId);
        
        BigDecimal value = reportingService.getInventoryValue(branchId);
        return ResponseEntity.ok(value);
    }
    
    // ========== Dashboard ==========
    
    @GetMapping("/dashboard")
    public ResponseEntity<DashboardResponse> getDashboard() {
        log.info("GET /api/reports/dashboard");
        
        DashboardResponse response = reportingService.getDashboard();
        return ResponseEntity.ok(response);
    }
    
    // ========== Health Check ==========
    
    @GetMapping("/health")
    public ResponseEntity<String> healthCheck() {
        return ResponseEntity.ok("Reporting Service is running");
    }
}
