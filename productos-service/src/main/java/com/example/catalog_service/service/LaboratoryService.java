package com.example.catalog_service.service;

import com.example.catalog_service.dto.request.LaboratoryRequest;
import com.example.catalog_service.dto.response.LaboratoryResponse;
import com.example.catalog_service.entity.Laboratory;
import com.example.catalog_service.exception.DuplicateResourceException;
import com.example.catalog_service.exception.ResourceNotFoundException;
import com.example.catalog_service.mapper.LaboratoryMapper;
import com.example.catalog_service.repository.LaboratoryRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
public class LaboratoryService {
    
    private final LaboratoryRepository laboratoryRepository;
    private final LaboratoryMapper laboratoryMapper;
    
    public LaboratoryService(LaboratoryRepository laboratoryRepository, LaboratoryMapper laboratoryMapper) {
        this.laboratoryRepository = laboratoryRepository;
        this.laboratoryMapper = laboratoryMapper;
    }
    
    @Transactional
    public LaboratoryResponse create(LaboratoryRequest request) {
        log.info("Creando laboratorio con nombre: {}", request.getName());
        
        // Validar nombre duplicado
        if (laboratoryRepository.existsByName(request.getName())) {
            log.error("El laboratorio con nombre {} ya existe", request.getName());
            throw new DuplicateResourceException("Ya existe un laboratorio con el nombre: " + request.getName());
        }
        
        Laboratory laboratory = laboratoryMapper.toEntity(request);
        Laboratory savedLaboratory = laboratoryRepository.save(laboratory);
        log.info("Laboratorio creado exitosamente con ID: {}", savedLaboratory.getId());
        
        return laboratoryMapper.toResponse(savedLaboratory);
    }
    
    @Transactional(readOnly = true)
    public List<LaboratoryResponse> findAll() {
        log.info("Obteniendo todos los laboratorios");
        List<Laboratory> laboratories = laboratoryRepository.findAll();
        return laboratories.stream()
                .map(laboratoryMapper::toResponse)
                .collect(Collectors.toList());
    }
    
    @Transactional(readOnly = true)
    public LaboratoryResponse findById(Long id) {
        log.info("Buscando laboratorio con ID: {}", id);
        Laboratory laboratory = laboratoryRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("No se encontró el laboratorio con ID: " + id));
        return laboratoryMapper.toResponse(laboratory);
    }
    
    @Transactional(readOnly = true)
    public LaboratoryResponse findByName(String name) {
        log.info("Buscando laboratorio con nombre: {}", name);
        Laboratory laboratory = laboratoryRepository.findByName(name)
                .orElseThrow(() -> new ResourceNotFoundException("No se encontró el laboratorio con nombre: " + name));
        return laboratoryMapper.toResponse(laboratory);
    }
    
    @Transactional(readOnly = true)
    public List<LaboratoryResponse> findActive() {
        log.info("Obteniendo laboratorios activos");
        List<Laboratory> laboratories = laboratoryRepository.findByIsActiveTrue();
        return laboratories.stream()
                .map(laboratoryMapper::toResponse)
                .collect(Collectors.toList());
    }
    
    @Transactional(readOnly = true)
    public List<LaboratoryResponse> search(String keyword) {
        log.info("Buscando laboratorios con palabra clave: {}", keyword);
        List<Laboratory> laboratories = laboratoryRepository.searchByKeyword(keyword);
        return laboratories.stream()
                .map(laboratoryMapper::toResponse)
                .collect(Collectors.toList());
    }
    
    @Transactional
    public LaboratoryResponse update(Long id, LaboratoryRequest request) {
        log.info("Actualizando laboratorio con ID: {}", id);
        
        Laboratory laboratory = laboratoryRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("No se encontró el laboratorio con ID: " + id));
        
        // Validar nombre duplicado (excluyendo el actual)
        if (laboratoryRepository.existsByNameAndIdNot(request.getName(), id)) {
            log.error("Ya existe otro laboratorio con el nombre: {}", request.getName());
            throw new DuplicateResourceException("Ya existe otro laboratorio con el nombre: " + request.getName());
        }
        
        laboratoryMapper.updateEntity(laboratory, request);
        Laboratory updatedLaboratory = laboratoryRepository.save(laboratory);
        log.info("Laboratorio actualizado exitosamente con ID: {}", updatedLaboratory.getId());
        
        return laboratoryMapper.toResponse(updatedLaboratory);
    }
    
    @Transactional
    public void delete(Long id) {
        log.info("Eliminando laboratorio con ID: {}", id);
        
        Laboratory laboratory = laboratoryRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("No se encontró el laboratorio con ID: " + id));
        
        laboratoryRepository.delete(laboratory);
        log.info("Laboratorio eliminado exitosamente con ID: {}", id);
    }
}
