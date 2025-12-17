package com.example.sales_service.controller;

import com.example.sales_service.dto.CreateSaleRequest;
import com.example.sales_service.entity.Sale;
import com.example.sales_service.enums.SaleStatus;
import com.example.sales_service.service.SaleService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;

@RestController
@RequestMapping("/api/sales")
@RequiredArgsConstructor
@Slf4j
public class SaleController {
    
    private final SaleService saleService;
    
    @PostMapping
    public ResponseEntity<Sale> createSale(@Valid @RequestBody CreateSaleRequest request) {
        log.info("Petición para crear venta en sucursal: {}", request.getBranchId());
        Sale sale = saleService.createSale(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(sale);
    }
    
    @GetMapping
    public ResponseEntity<Page<Sale>> getAllSales(Pageable pageable) {
        log.info("Petición para obtener todas las ventas");
        Page<Sale> sales = saleService.getAllSales(pageable);
        return ResponseEntity.ok(sales);
    }
    
    @GetMapping("/{id}")
    public ResponseEntity<Sale> getSaleById(@PathVariable Long id) {
        log.info("Petición para obtener venta con ID: {}", id);
        Sale sale = saleService.getSaleById(id);
        return ResponseEntity.ok(sale);
    }
    
    @GetMapping("/branch/{branchId}")
    public ResponseEntity<Page<Sale>> getSalesByBranch(
            @PathVariable Long branchId,
            Pageable pageable) {
        log.info("Petición para obtener ventas de la sucursal: {}", branchId);
        Page<Sale> sales = saleService.getSalesByBranch(branchId, pageable);
        return ResponseEntity.ok(sales);
    }
    
    @GetMapping("/customer/{customerId}")
    public ResponseEntity<Page<Sale>> getSalesByCustomer(
            @PathVariable Long customerId,
            Pageable pageable) {
        log.info("Petición para obtener ventas del cliente: {}", customerId);
        Page<Sale> sales = saleService.getSalesByCustomer(customerId, pageable);
        return ResponseEntity.ok(sales);
    }
    
    @GetMapping("/date-range")
    public ResponseEntity<Page<Sale>> getSalesByDateRange(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime start,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime end,
            Pageable pageable) {
        log.info("Petición para obtener ventas entre {} y {}", start, end);
        Page<Sale> sales = saleService.getSalesByDateRange(start, end, pageable);
        return ResponseEntity.ok(sales);
    }
    
    @PutMapping("/{id}/status")
    public ResponseEntity<Sale> updateSaleStatus(
            @PathVariable Long id,
            @RequestParam SaleStatus status) {
        log.info("Petición para actualizar estado de venta {} a {}", id, status);
        Sale sale = saleService.updateSaleStatus(id, status);
        return ResponseEntity.ok(sale);
    }
}
