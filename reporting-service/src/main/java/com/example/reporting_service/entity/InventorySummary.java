package com.example.reporting_service.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "inventory_summary")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class InventorySummary {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(name = "report_date", nullable = false)
    private LocalDate reportDate;
    
    @Column(name = "branch_id")
    private Long branchId;
    
    @Column(name = "total_products", nullable = false)
    private Integer totalProducts;
    
    @Column(name = "low_stock_products")
    private Integer lowStockProducts;
    
    @Column(name = "expiring_soon")
    private Integer expiringSoon;
    
    @Column(name = "inventory_value", precision = 12, scale = 2)
    private BigDecimal inventoryValue;
    
    @Column(name = "generated_at", nullable = false)
    private LocalDateTime generatedAt;
    
    @PrePersist
    protected void onCreate() {
        generatedAt = LocalDateTime.now();
    }
}
