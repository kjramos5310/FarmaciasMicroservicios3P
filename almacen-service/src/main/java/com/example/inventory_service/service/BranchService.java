package com.example.inventory_service.service;

import com.example.inventory_service.dto.request.BranchRequest;
import com.example.inventory_service.dto.response.BranchResponse;
import com.example.inventory_service.entity.Branch;
import com.example.inventory_service.exception.DuplicateResourceException;
import com.example.inventory_service.exception.ResourceNotFoundException;
import com.example.inventory_service.mapper.BranchMapper;
import com.example.inventory_service.repository.BranchRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class BranchService {
    
    private final BranchRepository branchRepository;
    private final BranchMapper branchMapper;
    
    @Transactional(readOnly = true)
    public List<BranchResponse> findAll() {
        log.debug("Obteniendo todas las sucursales");
        return branchRepository.findAll().stream()
                .map(branchMapper::toResponse)
                .collect(Collectors.toList());
    }
    
    @Transactional(readOnly = true)
    public BranchResponse findById(Long id) {
        log.debug("Buscando sucursal con ID: {}", id);
        Branch branch = branchRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Sucursal no encontrada con ID: " + id));
        return branchMapper.toResponse(branch);
    }
    
    @Transactional
    public BranchResponse create(BranchRequest request) {
        log.debug("Creando nueva sucursal con código: {}", request.getCode());
        
        if (branchRepository.existsByCode(request.getCode())) {
            throw new DuplicateResourceException("Ya existe una sucursal con el código: " + request.getCode());
        }
        
        Branch branch = branchMapper.toEntity(request);
        Branch savedBranch = branchRepository.save(branch);
        log.info("Sucursal creada exitosamente con ID: {}", savedBranch.getId());
        
        return branchMapper.toResponse(savedBranch);
    }
    
    @Transactional
    public BranchResponse update(Long id, BranchRequest request) {
        log.debug("Actualizando sucursal con ID: {}", id);
        
        Branch branch = branchRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Sucursal no encontrada con ID: " + id));
        
        // Verificar si el código ya existe en otra sucursal
        if (!branch.getCode().equals(request.getCode()) && branchRepository.existsByCode(request.getCode())) {
            throw new DuplicateResourceException("Ya existe una sucursal con el código: " + request.getCode());
        }
        
        branchMapper.updateEntity(branch, request);
        Branch updatedBranch = branchRepository.save(branch);
        log.info("Sucursal actualizada exitosamente con ID: {}", updatedBranch.getId());
        
        return branchMapper.toResponse(updatedBranch);
    }
    
    @Transactional
    public void delete(Long id) {
        log.debug("Eliminando sucursal con ID: {}", id);
        
        if (!branchRepository.existsById(id)) {
            throw new ResourceNotFoundException("Sucursal no encontrada con ID: " + id);
        }
        
        branchRepository.deleteById(id);
        log.info("Sucursal eliminada exitosamente con ID: {}", id);
    }
    
    @Transactional(readOnly = true)
    public Branch getBranchEntity(Long id) {
        return branchRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Sucursal no encontrada con ID: " + id));
    }
}
