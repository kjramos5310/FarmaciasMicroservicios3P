package com.example.reporting_service.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ReportRequest {
    private LocalDate startDate;
    private LocalDate endDate;
    private Long branchId; // Optional
}
