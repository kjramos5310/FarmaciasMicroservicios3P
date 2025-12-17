package com.example.catalog_service.controller;

import com.example.catalog_service.dto.request.LaboratoryRequest;
import com.example.catalog_service.dto.response.ApiResponse;
import com.example.catalog_service.dto.response.LaboratoryResponse;
import com.example.catalog_service.service.LaboratoryService;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/laboratories")
@CrossOrigin(origins = "*")
@Slf4j
public class LaboratoryController {
    
    private final LaboratoryService laboratoryService;
    
    public LaboratoryController(LaboratoryService laboratoryService) {
        this.laboratoryService = laboratoryService;
    }
    
    @PostMapping
    public ResponseEntity<ApiResponse<LaboratoryResponse>> create(@Valid @RequestBody LaboratoryRequest request) {
        log.info("Solicitud para crear laboratorio: {}", request.getName());
        LaboratoryResponse response = laboratoryService.create(request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("Laboratorio creado exitosamente", response));
    }
    
    @GetMapping
    public ResponseEntity<ApiResponse<List<LaboratoryResponse>>> findAll() {
        log.info("Solicitud para obtener todos los laboratorios");
        List<LaboratoryResponse> responses = laboratoryService.findAll();
        return ResponseEntity.ok(ApiResponse.success("Laboratorios obtenidos exitosamente", responses));
    }
    
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<LaboratoryResponse>> findById(@PathVariable Long id) {
        log.info("Solicitud para obtener laboratorio con ID: {}", id);
        LaboratoryResponse response = laboratoryService.findById(id);
        return ResponseEntity.ok(ApiResponse.success("Laboratorio obtenido exitosamente", response));
    }
    
    @GetMapping("/name/{name}")
    public ResponseEntity<ApiResponse<LaboratoryResponse>> findByName(@PathVariable String name) {
        log.info("Solicitud para obtener laboratorio con nombre: {}", name);
        LaboratoryResponse response = laboratoryService.findByName(name);
        return ResponseEntity.ok(ApiResponse.success("Laboratorio obtenido exitosamente", response));
    }
    
    @GetMapping("/active")
    public ResponseEntity<ApiResponse<List<LaboratoryResponse>>> findActive() {
        log.info("Solicitud para obtener laboratorios activos");
        List<LaboratoryResponse> responses = laboratoryService.findActive();
        return ResponseEntity.ok(ApiResponse.success("Laboratorios activos obtenidos exitosamente", responses));
    }
    
    @GetMapping("/search")
    public ResponseEntity<ApiResponse<List<LaboratoryResponse>>> search(@RequestParam String keyword) {
        log.info("Solicitud para buscar laboratorios con palabra clave: {}", keyword);
        List<LaboratoryResponse> responses = laboratoryService.search(keyword);
        return ResponseEntity.ok(ApiResponse.success("Búsqueda realizada exitosamente", responses));
    }
    
    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<LaboratoryResponse>> update(
            @PathVariable Long id,
            @Valid @RequestBody LaboratoryRequest request) {
        log.info("Solicitud para actualizar laboratorio con ID: {}", id);
        LaboratoryResponse response = laboratoryService.update(id, request);
        return ResponseEntity.ok(ApiResponse.success("Laboratorio actualizado exitosamente", response));
    }
    
    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> delete(@PathVariable Long id) {
        log.info("Solicitud para eliminar laboratorio con ID: {}", id);
        laboratoryService.delete(id);
        return ResponseEntity.ok(ApiResponse.success("Laboratorio eliminado exitosamente", null));
    }
}
