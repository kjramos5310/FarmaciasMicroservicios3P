package com.example.sales_service.dto;

import com.example.sales_service.enums.CustomerType;
import com.example.sales_service.enums.IdentificationType;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CustomerRequest {
    
    @NotBlank(message = "El número de identificación es obligatorio")
    private String identificationNumber;
    
    @NotNull(message = "El tipo de identificación es obligatorio")
    private IdentificationType identificationType;
    
    @NotBlank(message = "El nombre es obligatorio")
    private String firstName;
    
    @NotBlank(message = "El apellido es obligatorio")
    private String lastName;
    
    @NotBlank(message = "El email es obligatorio")
    @Email(message = "El email debe ser válido")
    private String email;
    
    @Pattern(regexp = "^[0-9]{10}$|^[0-9]{9}$|^$", message = "El teléfono debe tener 9 o 10 dígitos")
    private String phone;
    
    private String address;
    
    private String city;
    
    private LocalDate birthDate;
    
    private CustomerType type;
}
