package com.example.reporting_service.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "product_sales_report")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProductSalesReport {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(name = "product_id", nullable = false)
    private Long productId;
    
    @Column(name = "product_name", nullable = false, length = 200)
    private String productName;
    
    @Column(name = "product_code", length = 50)
    private String productCode;
    
    @Column(name = "report_date", nullable = false)
    private LocalDate reportDate;
    
    @Column(name = "branch_id")
    private Long branchId;
    
    @Column(name = "quantity_sold", nullable = false)
    private Integer quantitySold;
    
    @Column(name = "revenue", nullable = false, precision = 12, scale = 2)
    private BigDecimal revenue;
    
    @Column(name = "generated_at", nullable = false)
    private LocalDateTime generatedAt;
    
    @PrePersist
    protected void onCreate() {
        generatedAt = LocalDateTime.now();
    }
}
