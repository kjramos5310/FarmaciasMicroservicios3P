package com.example.inventory_service.service;

import com.example.inventory_service.dto.request.BatchRequest;
import com.example.inventory_service.dto.response.BatchResponse;
import com.example.inventory_service.entity.Batch;
import com.example.inventory_service.entity.Branch;
import com.example.inventory_service.mapper.BatchMapper;
import com.example.inventory_service.repository.BatchRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class BatchService {
    
    private final BatchRepository batchRepository;
    private final BranchService branchService;
    private final BatchMapper batchMapper;
    
    @Transactional
    public BatchResponse create(BatchRequest request) {
        log.debug("Creando nuevo lote: {}", request.getBatchNumber());
        
        // Validar que la cantidad sea positiva
        if (request.getQuantity() <= 0) {
            throw new IllegalArgumentException("La cantidad debe ser mayor a cero");
        }
        
        Branch branch = branchService.getBranchEntity(request.getBranchId());
        
        Batch batch = new Batch();
        batch.setBatchNumber(request.getBatchNumber());
        batch.setProductId(request.getProductId());
        batch.setBranch(branch);
        batch.setQuantity(request.getQuantity());
        batch.setExpirationDate(request.getExpirationDate());
        batch.setManufactureDate(request.getManufactureDate());
        batch.setStatus(request.getStatus());
        
        Batch savedBatch = batchRepository.save(batch);
        log.info("Lote creado exitosamente con ID: {}", savedBatch.getId());
        
        return batchMapper.toResponse(savedBatch);
    }
    
    @Transactional(readOnly = true)
    public List<BatchResponse> findAll() {
        log.debug("Obteniendo todos los lotes");
        return batchRepository.findAll().stream()
                .map(batchMapper::toResponse)
                .collect(Collectors.toList());
    }
    
    @Transactional(readOnly = true)
    public List<BatchResponse> findByBranch(Long branchId) {
        log.debug("Obteniendo lotes de la sucursal: {}", branchId);
        branchService.getBranchEntity(branchId); // Validar que la sucursal existe
        return batchRepository.findByBranchId(branchId).stream()
                .map(batchMapper::toResponse)
                .collect(Collectors.toList());
    }
    
    @Transactional(readOnly = true)
    public List<BatchResponse> findExpiringSoon() {
        log.debug("Obteniendo lotes que expiran pronto");
        LocalDate today = LocalDate.now();
        LocalDate thirtyDaysFromNow = today.plusDays(30);
        
        return batchRepository.findExpiringSoon(today, thirtyDaysFromNow).stream()
                .map(batchMapper::toResponse)
                .collect(Collectors.toList());
    }
}
