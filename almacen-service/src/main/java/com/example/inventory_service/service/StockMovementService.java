package com.example.inventory_service.service;

import com.example.inventory_service.dto.request.StockMovementRequest;
import com.example.inventory_service.dto.response.StockMovementResponse;
import com.example.inventory_service.entity.Branch;
import com.example.inventory_service.entity.StockMovement;
import com.example.inventory_service.entity.enums.MovementType;
import com.example.inventory_service.exception.InsufficientStockException;
import com.example.inventory_service.mapper.StockMovementMapper;
import com.example.inventory_service.repository.StockMovementRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class StockMovementService {
    
    private final StockMovementRepository movementRepository;
    private final BranchService branchService;
    private final StockService stockService;
    private final StockMovementMapper movementMapper;
    
    @Transactional
    public StockMovementResponse create(StockMovementRequest request) {
        log.debug("Creando movimiento de stock tipo {} para producto {} en sucursal {}", 
                request.getType(), request.getProductId(), request.getBranchId());
        
        // Validar que la cantidad sea positiva
        if (request.getQuantity() <= 0) {
            throw new IllegalArgumentException("La cantidad debe ser mayor a cero");
        }
        
        Branch branch = branchService.getBranchEntity(request.getBranchId());
        
        // Validar stock disponible para EXIT y TRANSFER
        if (request.getType() == MovementType.EXIT || request.getType() == MovementType.TRANSFER) {
            if (!stockService.checkAvailability(request.getBranchId(), request.getProductId(), request.getQuantity())) {
                throw new InsufficientStockException(
                        "Stock insuficiente del producto " + request.getProductId() + 
                        " en sucursal " + request.getBranchId());
            }
        }
        
        // Para TRANSFER, validar sucursal destino
        Branch destinationBranch = null;
        if (request.getType() == MovementType.TRANSFER) {
            if (request.getDestinationBranchId() == null) {
                throw new IllegalArgumentException("Se requiere sucursal destino para transferencias");
            }
            if (request.getDestinationBranchId().equals(request.getBranchId())) {
                throw new IllegalArgumentException("La sucursal destino debe ser diferente a la sucursal origen");
            }
            destinationBranch = branchService.getBranchEntity(request.getDestinationBranchId());
        }
        
        // Crear movimiento
        StockMovement movement = new StockMovement();
        movement.setBranch(branch);
        movement.setProductId(request.getProductId());
        movement.setType(request.getType());
        movement.setQuantity(request.getQuantity());
        movement.setReason(request.getReason());
        movement.setReference(request.getReference());
        movement.setDestinationBranch(destinationBranch);
        movement.setPerformedBy(request.getPerformedBy());
        
        StockMovement savedMovement = movementRepository.save(movement);
        
        // Actualizar stock según el tipo de movimiento
        switch (request.getType()) {
            case ENTRY:
            case RETURN:
                stockService.updateStockQuantity(request.getBranchId(), request.getProductId(), request.getQuantity());
                log.info("Stock incrementado en {} unidades", request.getQuantity());
                break;
                
            case EXIT:
                stockService.updateStockQuantity(request.getBranchId(), request.getProductId(), -request.getQuantity());
                log.info("Stock decrementado en {} unidades", request.getQuantity());
                break;
                
            case TRANSFER:
                stockService.updateStockQuantity(request.getBranchId(), request.getProductId(), -request.getQuantity());
                stockService.updateStockQuantity(request.getDestinationBranchId(), request.getProductId(), request.getQuantity());
                log.info("Stock transferido: {} unidades de sucursal {} a sucursal {}", 
                        request.getQuantity(), request.getBranchId(), request.getDestinationBranchId());
                break;
                
            case ADJUSTMENT:
                // El ajuste se maneja de forma especial: puede ser positivo o negativo
                stockService.updateStockQuantity(request.getBranchId(), request.getProductId(), request.getQuantity());
                log.info("Stock ajustado en {} unidades", request.getQuantity());
                break;
        }
        
        log.info("Movimiento de stock creado exitosamente con ID: {}", savedMovement.getId());
        return movementMapper.toResponse(savedMovement);
    }
    
    @Transactional(readOnly = true)
    public List<StockMovementResponse> findByBranch(Long branchId) {
        log.debug("Obteniendo movimientos de la sucursal: {}", branchId);
        branchService.getBranchEntity(branchId); // Validar que la sucursal existe
        return movementRepository.findByBranchId(branchId).stream()
                .map(movementMapper::toResponse)
                .collect(Collectors.toList());
    }
    
    @Transactional(readOnly = true)
    public List<StockMovementResponse> findByProduct(Long productId) {
        log.debug("Obteniendo movimientos del producto: {}", productId);
        return movementRepository.findByProductId(productId).stream()
                .map(movementMapper::toResponse)
                .collect(Collectors.toList());
    }
}
