package com.example.inventory_service.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "stock", uniqueConstraints = {
    @UniqueConstraint(columnNames = {"branch_id", "product_id"})
})
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Stock {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "branch_id", nullable = false)
    private Branch branch;
    
    @Column(name = "product_id", nullable = false)
    private Long productId;
    
    @Column(nullable = false)
    private Integer quantity;
    
    @Column(name = "minimum_stock", nullable = false)
    private Integer minimumStock;
    
    @Column(name = "maximum_stock", nullable = false)
    private Integer maximumStock;
    
    @Column(name = "last_restock_date")
    private LocalDateTime lastRestockDate;
    
    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
}
