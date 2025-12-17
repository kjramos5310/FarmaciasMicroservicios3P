package com.example.reporting_service.controller;

import com.example.reporting_service.dto.InventorySummaryResponse;
import com.example.reporting_service.dto.SalesSummaryResponse;
import com.example.reporting_service.entity.InventorySummary;
import com.example.reporting_service.entity.SalesSummary;
import com.example.reporting_service.mapper.InventorySummaryMapper;
import com.example.reporting_service.mapper.SalesSummaryMapper;
import com.example.reporting_service.repository.InventorySummaryRepository;
import com.example.reporting_service.repository.SalesSummaryRepository;
import com.example.reporting_service.service.SnapshotService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/snapshots")
@RequiredArgsConstructor
@Slf4j
@CrossOrigin(origins = "*")
public class SnapshotController {
    
    private final SnapshotService snapshotService;
    private final SalesSummaryRepository salesSummaryRepository;
    private final InventorySummaryRepository inventorySummaryRepository;
    private final SalesSummaryMapper salesSummaryMapper;
    private final InventorySummaryMapper inventorySummaryMapper;
    
    /**
     * Genera snapshots históricos para un rango de fechas
     */
    @PostMapping("/generate")
    public ResponseEntity<String> generateHistoricalSnapshots(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
        
        log.info("POST /api/snapshots/generate - startDate: {}, endDate: {}", startDate, endDate);
        
        snapshotService.generateHistoricalSnapshots(startDate, endDate);
        
        return ResponseEntity.ok("Snapshots históricos generados exitosamente");
    }
    
    /**
     * Obtiene snapshots de ventas almacenados
     */
    @GetMapping("/sales")
    public ResponseEntity<List<SalesSummaryResponse>> getSalesSnapshots(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate,
            @RequestParam(required = false) Long branchId) {
        
        log.info("GET /api/snapshots/sales - startDate: {}, endDate: {}, branchId: {}", 
                startDate, endDate, branchId);
        
        List<SalesSummary> summaries;
        
        if (branchId != null) {
            summaries = salesSummaryRepository.findByDateRangeAndBranch(startDate, endDate, branchId);
        } else {
            summaries = salesSummaryRepository.findByDateRange(startDate, endDate);
        }
        
        List<SalesSummaryResponse> responses = summaries.stream()
                .map(salesSummaryMapper::toResponse)
                .collect(Collectors.toList());
        
        return ResponseEntity.ok(responses);
    }
    
    /**
     * Obtiene snapshots de inventario almacenados
     */
    @GetMapping("/inventory")
    public ResponseEntity<List<InventorySummaryResponse>> getInventorySnapshots(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate reportDate,
            @RequestParam(required = false) Long branchId) {
        
        log.info("GET /api/snapshots/inventory - reportDate: {}, branchId: {}", reportDate, branchId);
        
        List<InventorySummary> summaries;
        
        if (branchId != null) {
            summaries = inventorySummaryRepository.findByReportDateAndBranch(reportDate, branchId)
                    .map(List::of)
                    .orElse(List.of());
        } else {
            summaries = inventorySummaryRepository.findByReportDate(reportDate);
        }
        
        List<InventorySummaryResponse> responses = summaries.stream()
                .map(inventorySummaryMapper::toResponse)
                .collect(Collectors.toList());
        
        return ResponseEntity.ok(responses);
    }
}
