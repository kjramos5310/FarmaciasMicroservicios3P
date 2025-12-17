package com.example.reporting_service.service;

import com.example.reporting_service.entity.InventorySummary;
import com.example.reporting_service.entity.SalesSummary;
import com.example.reporting_service.repository.InventorySummaryRepository;
import com.example.reporting_service.repository.SalesSummaryRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

/**
 * Servicio opcional para generar snapshots automáticos de reportes.
 * Descomentar @EnableScheduling en la clase principal para activar.
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class SnapshotService {
    
    private final ReportingService reportingService;
    private final SalesSummaryRepository salesSummaryRepository;
    private final InventorySummaryRepository inventorySummaryRepository;
    
    /**
     * Genera snapshots diarios automáticos cada noche a medianoche.
     * Para activar, agregar @EnableScheduling en ReportingServiceApplication.
     */
    @Scheduled(cron = "0 0 0 * * *") // Todos los días a medianoche
    public void generateDailySnapshots() {
        log.info("Starting daily snapshot generation...");
        
        LocalDate yesterday = LocalDate.now().minusDays(1);
        
        try {
            generateSalesSnapshots(yesterday);
            generateInventorySnapshots(yesterday);
            log.info("Daily snapshots generated successfully for date: {}", yesterday);
        } catch (Exception e) {
            log.error("Error generating daily snapshots: {}", e.getMessage(), e);
        }
    }
    
    private void generateSalesSnapshots(LocalDate date) {
        // Obtener lista de sucursales (esto debería venir de un servicio de sucursales)
        List<Long> branchIds = List.of(1L, 2L, 3L); // Ejemplo
        
        for (Long branchId : branchIds) {
            try {
                var summary = reportingService.getSalesSummary(date, date, branchId);
                
                SalesSummary entity = new SalesSummary();
                entity.setReportDate(date);
                entity.setBranchId(branchId);
                entity.setTotalSales(summary.getTotalSales());
                entity.setTotalRevenue(summary.getTotalRevenue());
                entity.setAverageTicket(summary.getAverageTicket());
                entity.setTotalItems(summary.getTotalItems());
                entity.setUniqueCustomers(summary.getUniqueCustomers());
                
                salesSummaryRepository.save(entity);
                log.info("Sales snapshot saved for branch {} on {}", branchId, date);
            } catch (Exception e) {
                log.error("Error generating sales snapshot for branch {}: {}", branchId, e.getMessage());
            }
        }
    }
    
    private void generateInventorySnapshots(LocalDate date) {
        // Obtener lista de sucursales
        List<Long> branchIds = List.of(1L, 2L, 3L); // Ejemplo
        
        for (Long branchId : branchIds) {
            try {
                var summary = reportingService.getInventorySummary(branchId);
                
                InventorySummary entity = new InventorySummary();
                entity.setReportDate(date);
                entity.setBranchId(branchId);
                entity.setTotalProducts(summary.getTotalProducts());
                entity.setLowStockProducts(summary.getLowStockProducts());
                entity.setExpiringSoon(summary.getExpiringSoon());
                entity.setInventoryValue(summary.getInventoryValue());
                
                inventorySummaryRepository.save(entity);
                log.info("Inventory snapshot saved for branch {} on {}", branchId, date);
            } catch (Exception e) {
                log.error("Error generating inventory snapshot for branch {}: {}", branchId, e.getMessage());
            }
        }
    }
    
    /**
     * Genera snapshots manualmente para un rango de fechas.
     * Útil para regenerar históricos.
     */
    public void generateHistoricalSnapshots(LocalDate startDate, LocalDate endDate) {
        log.info("Generating historical snapshots from {} to {}", startDate, endDate);
        
        LocalDate currentDate = startDate;
        while (!currentDate.isAfter(endDate)) {
            generateSalesSnapshots(currentDate);
            generateInventorySnapshots(currentDate);
            currentDate = currentDate.plusDays(1);
        }
        
        log.info("Historical snapshots generation completed");
    }
}
