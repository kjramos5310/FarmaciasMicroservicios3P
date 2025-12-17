package com.example.reporting_service.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "sales_summary")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class SalesSummary {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(name = "report_date", nullable = false)
    private LocalDate reportDate;
    
    @Column(name = "branch_id")
    private Long branchId;
    
    @Column(name = "total_sales", nullable = false)
    private Integer totalSales;
    
    @Column(name = "total_revenue", nullable = false, precision = 12, scale = 2)
    private BigDecimal totalRevenue;
    
    @Column(name = "average_ticket", precision = 10, scale = 2)
    private BigDecimal averageTicket;
    
    @Column(name = "total_items")
    private Integer totalItems;
    
    @Column(name = "unique_customers")
    private Integer uniqueCustomers;
    
    @Column(name = "generated_at", nullable = false)
    private LocalDateTime generatedAt;
    
    @PrePersist
    protected void onCreate() {
        generatedAt = LocalDateTime.now();
    }
}
