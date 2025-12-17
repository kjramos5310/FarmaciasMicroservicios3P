package com.example.sales_service.service;

import com.example.sales_service.dto.PrescriptionRequest;
import com.example.sales_service.entity.Customer;
import com.example.sales_service.entity.Prescription;
import com.example.sales_service.enums.PrescriptionStatus;
import com.example.sales_service.mapper.PrescriptionMapper;
import com.example.sales_service.repository.CustomerRepository;
import com.example.sales_service.repository.PrescriptionRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.Year;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class PrescriptionService {
    
    private final PrescriptionRepository prescriptionRepository;
    private final CustomerRepository customerRepository;
    private final PrescriptionMapper prescriptionMapper;
    
    @Transactional
    public Prescription createPrescription(PrescriptionRequest request) {
        log.info("Creando prescripción para cliente ID: {}", request.getCustomerId());
        
        Customer customer = customerRepository.findById(request.getCustomerId())
                .orElseThrow(() -> new IllegalArgumentException("Cliente no encontrado con ID: " + request.getCustomerId()));
        
        Prescription prescription = prescriptionMapper.toEntity(request, customer);
        prescription.setPrescriptionNumber(generatePrescriptionNumber());
        
        Prescription saved = prescriptionRepository.save(prescription);
        log.info("Prescripción creada exitosamente con número: {}", saved.getPrescriptionNumber());
        return saved;
    }
    
    @Transactional(readOnly = true)
    public Prescription getPrescriptionById(Long id) {
        log.info("Buscando prescripción con ID: {}", id);
        return prescriptionRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Prescripción no encontrada con ID: " + id));
    }
    
    @Transactional(readOnly = true)
    public Page<Prescription> getPrescriptionsByCustomer(Long customerId, Pageable pageable) {
        log.info("Obteniendo prescripciones del cliente: {}", customerId);
        return prescriptionRepository.findByCustomerId(customerId, pageable);
    }
    
    @Transactional
    public Prescription updatePrescriptionStatus(Long id, PrescriptionStatus status) {
        log.info("Actualizando estado de prescripción ID: {} a {}", id, status);
        Prescription prescription = getPrescriptionById(id);
        prescription.setStatus(status);
        Prescription updated = prescriptionRepository.save(prescription);
        log.info("Estado de prescripción actualizado exitosamente");
        return updated;
    }
    
    @Transactional(readOnly = true)
    public List<Prescription> getActivePrescriptionsByCustomer(Long customerId) {
        log.info("Obteniendo prescripciones activas del cliente: {}", customerId);
        return prescriptionRepository.findByCustomerIdAndStatus(customerId, PrescriptionStatus.ACTIVE);
    }
    
    private String generatePrescriptionNumber() {
        Year currentYear = Year.now();
        Long count = prescriptionRepository.count();
        String number = String.format("RX-%d-%06d", currentYear.getValue(), count + 1);
        
        while (prescriptionRepository.existsByPrescriptionNumber(number)) {
            count++;
            number = String.format("RX-%d-%06d", currentYear.getValue(), count + 1);
        }
        
        log.debug("Número de prescripción generado: {}", number);
        return number;
    }
    
    public boolean isPrescriptionValid(Prescription prescription) {
        return prescription != null &&
               prescription.getStatus() == PrescriptionStatus.ACTIVE &&
               prescription.getExpirationDate().isAfter(LocalDate.now());
    }
}
