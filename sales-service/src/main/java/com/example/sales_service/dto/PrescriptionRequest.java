package com.example.sales_service.dto;

import com.example.sales_service.enums.PrescriptionStatus;
import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PrescriptionRequest {
    
    @NotNull(message = "El ID del cliente es obligatorio")
    private Long customerId;
    
    @NotBlank(message = "El nombre del doctor es obligatorio")
    private String doctorName;
    
    @NotBlank(message = "La licencia del doctor es obligatoria")
    private String doctorLicense;
    
    private String doctorSpecialty;
    
    @NotNull(message = "La fecha de emisión es obligatoria")
    private LocalDate issueDate;
    
    @NotNull(message = "La fecha de expiración es obligatoria")
    @Future(message = "La fecha de expiración debe ser futura")
    private LocalDate expirationDate;
    
    private String diagnosis;
    
    private String notes;
    
    private PrescriptionStatus status;
}
