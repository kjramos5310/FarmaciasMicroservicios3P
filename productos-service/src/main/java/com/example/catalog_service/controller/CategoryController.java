package com.example.catalog_service.controller;

import com.example.catalog_service.dto.request.CategoryRequest;
import com.example.catalog_service.dto.response.ApiResponse;
import com.example.catalog_service.dto.response.CategoryResponse;
import com.example.catalog_service.service.CategoryService;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/categories")
@CrossOrigin(origins = "*")
@Slf4j
public class CategoryController {
    
    private final CategoryService categoryService;
    
    public CategoryController(CategoryService categoryService) {
        this.categoryService = categoryService;
    }
    
    @PostMapping
    public ResponseEntity<ApiResponse<CategoryResponse>> create(@Valid @RequestBody CategoryRequest request) {
        log.info("Solicitud para crear categoría: {}", request.getCode());
        CategoryResponse response = categoryService.create(request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("Categoría creada exitosamente", response));
    }
    
    @GetMapping
    public ResponseEntity<ApiResponse<List<CategoryResponse>>> findAll() {
        log.info("Solicitud para obtener todas las categorías");
        List<CategoryResponse> responses = categoryService.findAll();
        return ResponseEntity.ok(ApiResponse.success("Categorías obtenidas exitosamente", responses));
    }
    
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<CategoryResponse>> findById(@PathVariable Long id) {
        log.info("Solicitud para obtener categoría con ID: {}", id);
        CategoryResponse response = categoryService.findById(id);
        return ResponseEntity.ok(ApiResponse.success("Categoría obtenida exitosamente", response));
    }
    
    @GetMapping("/code/{code}")
    public ResponseEntity<ApiResponse<CategoryResponse>> findByCode(@PathVariable String code) {
        log.info("Solicitud para obtener categoría con código: {}", code);
        CategoryResponse response = categoryService.findByCode(code);
        return ResponseEntity.ok(ApiResponse.success("Categoría obtenida exitosamente", response));
    }
    
    @GetMapping("/active")
    public ResponseEntity<ApiResponse<List<CategoryResponse>>> findActive() {
        log.info("Solicitud para obtener categorías activas");
        List<CategoryResponse> responses = categoryService.findActive();
        return ResponseEntity.ok(ApiResponse.success("Categorías activas obtenidas exitosamente", responses));
    }
    
    @GetMapping("/search")
    public ResponseEntity<ApiResponse<List<CategoryResponse>>> search(@RequestParam String keyword) {
        log.info("Solicitud para buscar categorías con palabra clave: {}", keyword);
        List<CategoryResponse> responses = categoryService.search(keyword);
        return ResponseEntity.ok(ApiResponse.success("Búsqueda realizada exitosamente", responses));
    }
    
    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<CategoryResponse>> update(
            @PathVariable Long id,
            @Valid @RequestBody CategoryRequest request) {
        log.info("Solicitud para actualizar categoría con ID: {}", id);
        CategoryResponse response = categoryService.update(id, request);
        return ResponseEntity.ok(ApiResponse.success("Categoría actualizada exitosamente", response));
    }
    
    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> delete(@PathVariable Long id) {
        log.info("Solicitud para eliminar categoría con ID: {}", id);
        categoryService.delete(id);
        return ResponseEntity.ok(ApiResponse.success("Categoría eliminada exitosamente", null));
    }
}
