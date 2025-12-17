package com.example.inventory_service.service;

import com.example.inventory_service.dto.request.StockRequest;
import com.example.inventory_service.dto.response.StockResponse;
import com.example.inventory_service.entity.Branch;
import com.example.inventory_service.entity.Stock;
import com.example.inventory_service.exception.ResourceNotFoundException;
import com.example.inventory_service.mapper.StockMapper;
import com.example.inventory_service.repository.StockRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class StockService {
    
    private final StockRepository stockRepository;
    private final BranchService branchService;
    private final StockMapper stockMapper;
    
    @Transactional(readOnly = true)
    public List<StockResponse> findAll() {
        log.debug("Obteniendo todo el stock");
        return stockRepository.findAll().stream()
                .map(stockMapper::toResponse)
                .collect(Collectors.toList());
    }
    
    @Transactional(readOnly = true)
    public List<StockResponse> findByBranch(Long branchId) {
        log.debug("Obteniendo stock de la sucursal: {}", branchId);
        branchService.getBranchEntity(branchId); // Validar que la sucursal existe
        return stockRepository.findByBranchId(branchId).stream()
                .map(stockMapper::toResponse)
                .collect(Collectors.toList());
    }
    
    @Transactional(readOnly = true)
    public StockResponse findByBranchAndProduct(Long branchId, Long productId) {
        log.debug("Obteniendo stock del producto {} en sucursal {}", productId, branchId);
        Stock stock = stockRepository.findByBranchIdAndProductId(branchId, productId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Stock no encontrado para producto " + productId + " en sucursal " + branchId));
        return stockMapper.toResponse(stock);
    }
    
    @Transactional
    public StockResponse createOrUpdate(StockRequest request) {
        log.debug("Creando o actualizando stock para producto {} en sucursal {}", 
                request.getProductId(), request.getBranchId());
        
        // Validar que la cantidad no sea negativa
        if (request.getQuantity() < 0) {
            throw new IllegalArgumentException("La cantidad no puede ser negativa");
        }
        
        // Validar que el máximo sea mayor al mínimo
        if (request.getMaximumStock() <= request.getMinimumStock()) {
            throw new IllegalArgumentException("El stock máximo debe ser mayor al stock mínimo");
        }
        
        Branch branch = branchService.getBranchEntity(request.getBranchId());
        
        Stock stock = stockRepository.findByBranchIdAndProductId(request.getBranchId(), request.getProductId())
                .orElse(new Stock());
        
        boolean isNew = stock.getId() == null;
        
        if (isNew) {
            stock.setBranch(branch);
            stock.setProductId(request.getProductId());
            stock.setLastRestockDate(LocalDateTime.now());
        }
        
        stockMapper.updateFromRequest(stock, request);
        
        Stock savedStock = stockRepository.save(stock);
        log.info("Stock {} exitosamente para producto {} en sucursal {}", 
                isNew ? "creado" : "actualizado", request.getProductId(), request.getBranchId());
        
        return stockMapper.toResponse(savedStock);
    }
    
    @Transactional(readOnly = true)
    public List<StockResponse> findLowStockAlerts() {
        log.debug("Obteniendo alertas de stock bajo");
        return stockRepository.findAllBelowMinimum().stream()
                .map(stockMapper::toResponse)
                .collect(Collectors.toList());
    }
    
    @Transactional(readOnly = true)
    public boolean checkAvailability(Long branchId, Long productId, Integer quantity) {
        log.debug("Verificando disponibilidad de {} unidades del producto {} en sucursal {}", 
                quantity, productId, branchId);
        
        Stock stock = stockRepository.findByBranchIdAndProductId(branchId, productId)
                .orElse(null);
        
        return stock != null && stock.getQuantity() >= quantity;
    }
    
    @Transactional
    public void updateStockQuantity(Long branchId, Long productId, Integer quantityChange) {
        log.debug("Actualizando cantidad de stock: {} para producto {} en sucursal {}", 
                quantityChange, productId, branchId);
        
        Stock stock = stockRepository.findByBranchIdAndProductId(branchId, productId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Stock no encontrado para producto " + productId + " en sucursal " + branchId));
        
        Integer newQuantity = stock.getQuantity() + quantityChange;
        
        if (newQuantity < 0) {
            throw new IllegalArgumentException("La cantidad resultante no puede ser negativa");
        }
        
        stock.setQuantity(newQuantity);
        
        if (quantityChange > 0) {
            stock.setLastRestockDate(LocalDateTime.now());
        }
        
        stockRepository.save(stock);
        log.info("Stock actualizado para producto {} en sucursal {}: nueva cantidad = {}", 
                productId, branchId, newQuantity);
    }
}
