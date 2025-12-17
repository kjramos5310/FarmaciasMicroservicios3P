package com.example.sales_service.controller;

import com.example.sales_service.dto.PrescriptionRequest;
import com.example.sales_service.entity.Prescription;
import com.example.sales_service.enums.PrescriptionStatus;
import com.example.sales_service.service.PrescriptionService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/prescriptions")
@RequiredArgsConstructor
@Slf4j
public class PrescriptionController {
    
    private final PrescriptionService prescriptionService;
    
    @PostMapping
    public ResponseEntity<Prescription> createPrescription(@Valid @RequestBody PrescriptionRequest request) {
        log.info("Petición para crear prescripción para cliente: {}", request.getCustomerId());
        Prescription prescription = prescriptionService.createPrescription(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(prescription);
    }
    
    @GetMapping("/{id}")
    public ResponseEntity<Prescription> getPrescriptionById(@PathVariable Long id) {
        log.info("Petición para obtener prescripción con ID: {}", id);
        Prescription prescription = prescriptionService.getPrescriptionById(id);
        return ResponseEntity.ok(prescription);
    }
    
    @GetMapping("/customer/{customerId}")
    public ResponseEntity<Page<Prescription>> getPrescriptionsByCustomer(
            @PathVariable Long customerId,
            Pageable pageable) {
        log.info("Petición para obtener prescripciones del cliente: {}", customerId);
        Page<Prescription> prescriptions = prescriptionService.getPrescriptionsByCustomer(customerId, pageable);
        return ResponseEntity.ok(prescriptions);
    }
    
    @PutMapping("/{id}/status")
    public ResponseEntity<Prescription> updatePrescriptionStatus(
            @PathVariable Long id,
            @RequestParam PrescriptionStatus status) {
        log.info("Petición para actualizar estado de prescripción {} a {}", id, status);
        Prescription prescription = prescriptionService.updatePrescriptionStatus(id, status);
        return ResponseEntity.ok(prescription);
    }
}
