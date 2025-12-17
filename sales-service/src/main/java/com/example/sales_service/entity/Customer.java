package com.example.sales_service.entity;

import com.example.sales_service.enums.CustomerType;
import com.example.sales_service.enums.IdentificationType;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "customers")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Customer {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(unique = true, nullable = false)
    private String identificationNumber;
    
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private IdentificationType identificationType;
    
    @Column(nullable = false)
    private String firstName;
    
    @Column(nullable = false)
    private String lastName;
    
    @Column(nullable = false)
    private String email;
    
    private String phone;
    
    private String address;
    
    private String city;
    
    private LocalDate birthDate;
    
    @Column(nullable = false)
    private Integer loyaltyPoints = 0;
    
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private CustomerType type = CustomerType.REGULAR;
    
    @CreationTimestamp
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;
    
    @UpdateTimestamp
    @Column(nullable = false)
    private LocalDateTime updatedAt;
}
