package com.example.sales_service.mapper;

import com.example.sales_service.dto.CustomerRequest;
import com.example.sales_service.entity.Customer;
import com.example.sales_service.enums.CustomerType;
import org.springframework.stereotype.Component;

@Component
public class CustomerMapper {
    
    public Customer toEntity(CustomerRequest request) {
        if (request == null) {
            return null;
        }
        
        Customer customer = new Customer();
        customer.setIdentificationNumber(request.getIdentificationNumber());
        customer.setIdentificationType(request.getIdentificationType());
        customer.setFirstName(request.getFirstName());
        customer.setLastName(request.getLastName());
        customer.setEmail(request.getEmail());
        customer.setPhone(request.getPhone());
        customer.setAddress(request.getAddress());
        customer.setCity(request.getCity());
        customer.setBirthDate(request.getBirthDate());
        customer.setType(request.getType() != null ? request.getType() : CustomerType.REGULAR);
        customer.setLoyaltyPoints(0);
        
        return customer;
    }
    
    public void updateEntity(Customer customer, CustomerRequest request) {
        if (request == null || customer == null) {
            return;
        }
        
        customer.setIdentificationNumber(request.getIdentificationNumber());
        customer.setIdentificationType(request.getIdentificationType());
        customer.setFirstName(request.getFirstName());
        customer.setLastName(request.getLastName());
        customer.setEmail(request.getEmail());
        customer.setPhone(request.getPhone());
        customer.setAddress(request.getAddress());
        customer.setCity(request.getCity());
        customer.setBirthDate(request.getBirthDate());
        if (request.getType() != null) {
            customer.setType(request.getType());
        }
    }
}
