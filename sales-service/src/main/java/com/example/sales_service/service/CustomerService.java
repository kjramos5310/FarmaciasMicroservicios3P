package com.example.sales_service.service;

import com.example.sales_service.dto.CustomerRequest;
import com.example.sales_service.entity.Customer;
import com.example.sales_service.entity.Sale;
import com.example.sales_service.mapper.CustomerMapper;
import com.example.sales_service.repository.CustomerRepository;
import com.example.sales_service.repository.SaleRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
public class CustomerService {
    
    private final CustomerRepository customerRepository;
    private final SaleRepository saleRepository;
    private final CustomerMapper customerMapper;
    
    @Transactional
    public Customer createCustomer(CustomerRequest request) {
        log.info("Creando cliente con identificación: {}", request.getIdentificationNumber());
        
        if (customerRepository.existsByIdentificationNumber(request.getIdentificationNumber())) {
            throw new IllegalArgumentException("Ya existe un cliente con ese número de identificación");
        }
        
        Customer customer = customerMapper.toEntity(request);
        Customer saved = customerRepository.save(customer);
        
        log.info("Cliente creado exitosamente con ID: {}", saved.getId());
        return saved;
    }
    
    @Transactional(readOnly = true)
    public Page<Customer> getAllCustomers(Pageable pageable) {
        log.info("Obteniendo todos los clientes - página: {}", pageable.getPageNumber());
        return customerRepository.findAll(pageable);
    }
    
    @Transactional(readOnly = true)
    public Customer getCustomerById(Long id) {
        log.info("Buscando cliente con ID: {}", id);
        return customerRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Cliente no encontrado con ID: " + id));
    }
    
    @Transactional
    public Customer updateCustomer(Long id, CustomerRequest request) {
        log.info("Actualizando cliente con ID: {}", id);
        
        Customer customer = getCustomerById(id);
        
        if (!customer.getIdentificationNumber().equals(request.getIdentificationNumber()) &&
            customerRepository.existsByIdentificationNumber(request.getIdentificationNumber())) {
            throw new IllegalArgumentException("Ya existe un cliente con ese número de identificación");
        }
        
        customerMapper.updateEntity(customer, request);
        Customer updated = customerRepository.save(customer);
        
        log.info("Cliente actualizado exitosamente con ID: {}", id);
        return updated;
    }
    
    @Transactional
    public void deleteCustomer(Long id) {
        log.info("Eliminando cliente con ID: {}", id);
        Customer customer = getCustomerById(id);
        customerRepository.delete(customer);
        log.info("Cliente eliminado exitosamente con ID: {}", id);
    }
    
    @Transactional(readOnly = true)
    public Page<Sale> getCustomerHistory(Long customerId, Pageable pageable) {
        log.info("Obteniendo historial de ventas del cliente: {}", customerId);
        getCustomerById(customerId); // Verificar que existe
        return saleRepository.findByCustomerId(customerId, pageable);
    }
    
    @Transactional
    public Customer updateLoyaltyPoints(Long customerId, Integer points) {
        log.info("Actualizando puntos de lealtad del cliente: {} a {} puntos", customerId, points);
        Customer customer = getCustomerById(customerId);
        customer.setLoyaltyPoints(points);
        Customer updated = customerRepository.save(customer);
        log.info("Puntos de lealtad actualizados exitosamente");
        return updated;
    }
}
