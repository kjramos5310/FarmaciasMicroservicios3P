package com.example.inventory_service.entity;

import com.example.inventory_service.entity.enums.BranchStatus;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;
import java.time.LocalTime;

@Entity
@Table(name = "branches")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Branch {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(unique = true, nullable = false, length = 50)
    private String code;
    
    @Column(nullable = false, length = 200)
    private String name;
    
    @Column(length = 300)
    private String address;
    
    @Column(length = 100)
    private String city;
    
    @Column(length = 100)
    private String province;
    
    @Column(length = 20)
    private String phone;
    
    @Column(length = 100)
    private String email;
    
    @Column(name = "manager_name", length = 150)
    private String managerName;
    
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private BranchStatus status;
    
    @Column(name = "opening_time")
    private LocalTime openingTime;
    
    @Column(name = "closing_time")
    private LocalTime closingTime;
    
    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;
}
