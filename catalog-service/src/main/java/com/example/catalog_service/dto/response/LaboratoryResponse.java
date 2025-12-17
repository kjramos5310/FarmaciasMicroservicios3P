package com.example.catalog_service.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class LaboratoryResponse {
    private Long id;
    private String name;
    private String country;
    private String contactEmail;
    private String phone;
    private String website;
    private Boolean isActive;
    private LocalDateTime createdAt;
}
