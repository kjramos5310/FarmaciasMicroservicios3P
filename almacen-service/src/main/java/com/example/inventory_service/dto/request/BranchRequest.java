package com.example.inventory_service.dto.request;

import com.example.inventory_service.entity.enums.BranchStatus;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class BranchRequest {
    
    @NotBlank(message = "El código es obligatorio")
    @Size(max = 50, message = "El código no puede exceder 50 caracteres")
    private String code;
    
    @NotBlank(message = "El nombre es obligatorio")
    @Size(max = 200, message = "El nombre no puede exceder 200 caracteres")
    private String name;
    
    @Size(max = 300, message = "La dirección no puede exceder 300 caracteres")
    private String address;
    
    @Size(max = 100, message = "La ciudad no puede exceder 100 caracteres")
    private String city;
    
    @Size(max = 100, message = "La provincia no puede exceder 100 caracteres")
    private String province;
    
    @Pattern(regexp = "^[0-9+\\-() ]*$", message = "Formato de teléfono inválido")
    @Size(max = 20, message = "El teléfono no puede exceder 20 caracteres")
    private String phone;
    
    @Email(message = "Email inválido")
    @Size(max = 100, message = "El email no puede exceder 100 caracteres")
    private String email;
    
    @Size(max = 150, message = "El nombre del gerente no puede exceder 150 caracteres")
    private String managerName;
    
    @NotNull(message = "El estado es obligatorio")
    private BranchStatus status;
    
    private LocalTime openingTime;
    
    private LocalTime closingTime;
}
