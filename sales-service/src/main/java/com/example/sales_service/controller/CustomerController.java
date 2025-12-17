package com.example.sales_service.controller;

import com.example.sales_service.dto.CustomerRequest;
import com.example.sales_service.entity.Customer;
import com.example.sales_service.entity.Sale;
import com.example.sales_service.service.CustomerService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/customers")
@RequiredArgsConstructor
@Slf4j
public class CustomerController {
    
    private final CustomerService customerService;
    
    @PostMapping
    public ResponseEntity<Customer> createCustomer(@Valid @RequestBody CustomerRequest request) {
        log.info("Petición para crear cliente: {}", request.getIdentificationNumber());
        Customer customer = customerService.createCustomer(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(customer);
    }
    
    @GetMapping
    public ResponseEntity<Page<Customer>> getAllCustomers(Pageable pageable) {
        log.info("Petición para obtener todos los clientes");
        Page<Customer> customers = customerService.getAllCustomers(pageable);
        return ResponseEntity.ok(customers);
    }
    
    @GetMapping("/{id}")
    public ResponseEntity<Customer> getCustomerById(@PathVariable Long id) {
        log.info("Petición para obtener cliente con ID: {}", id);
        Customer customer = customerService.getCustomerById(id);
        return ResponseEntity.ok(customer);
    }
    
    @PutMapping("/{id}")
    public ResponseEntity<Customer> updateCustomer(
            @PathVariable Long id,
            @Valid @RequestBody CustomerRequest request) {
        log.info("Petición para actualizar cliente con ID: {}", id);
        Customer customer = customerService.updateCustomer(id, request);
        return ResponseEntity.ok(customer);
    }
    
    @DeleteMapping("/{id}")
    public ResponseEntity<Map<String, String>> deleteCustomer(@PathVariable Long id) {
        log.info("Petición para eliminar cliente con ID: {}", id);
        customerService.deleteCustomer(id);
        Map<String, String> response = new HashMap<>();
        response.put("message", "Cliente eliminado exitosamente");
        return ResponseEntity.ok(response);
    }
    
    @GetMapping("/{id}/history")
    public ResponseEntity<Page<Sale>> getCustomerHistory(
            @PathVariable Long id,
            Pageable pageable) {
        log.info("Petición para obtener historial del cliente: {}", id);
        Page<Sale> history = customerService.getCustomerHistory(id, pageable);
        return ResponseEntity.ok(history);
    }
    
    @PutMapping("/{id}/loyalty")
    public ResponseEntity<Customer> updateLoyaltyPoints(
            @PathVariable Long id,
            @RequestParam Integer points) {
        log.info("Petición para actualizar puntos de lealtad del cliente: {}", id);
        Customer customer = customerService.updateLoyaltyPoints(id, points);
        return ResponseEntity.ok(customer);
    }
}
