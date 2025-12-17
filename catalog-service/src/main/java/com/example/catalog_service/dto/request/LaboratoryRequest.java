package com.example.catalog_service.dto.request;

import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class LaboratoryRequest {
    
    @NotBlank(message = "El nombre es obligatorio")
    @Size(max = 200, message = "El nombre no puede exceder 200 caracteres")
    private String name;
    
    @NotBlank(message = "El país es obligatorio")
    @Size(max = 100, message = "El país no puede exceder 100 caracteres")
    private String country;
    
    @NotBlank(message = "El email de contacto es obligatorio")
    @Email(message = "El email debe ser válido")
    private String contactEmail;
    
    @Size(max = 20, message = "El teléfono no puede exceder 20 caracteres")
    private String phone;
    
    @Size(max = 255, message = "El sitio web no puede exceder 255 caracteres")
    private String website;
    
    @NotNull(message = "El estado activo es obligatorio")
    private Boolean isActive;
}
