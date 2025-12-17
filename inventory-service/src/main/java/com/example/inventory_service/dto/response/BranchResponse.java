package com.example.inventory_service.dto.response;

import com.example.inventory_service.entity.enums.BranchStatus;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.time.LocalTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class BranchResponse {
    
    private Long id;
    private String code;
    private String name;
    private String address;
    private String city;
    private String province;
    private String phone;
    private String email;
    private String managerName;
    private BranchStatus status;
    private LocalTime openingTime;
    private LocalTime closingTime;
    private LocalDateTime createdAt;
}
