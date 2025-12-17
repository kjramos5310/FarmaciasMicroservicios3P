package com.example.catalog_service.service;

import com.example.catalog_service.dto.request.CategoryRequest;
import com.example.catalog_service.dto.response.CategoryResponse;
import com.example.catalog_service.entity.Category;
import com.example.catalog_service.exception.DuplicateResourceException;
import com.example.catalog_service.exception.ResourceNotFoundException;
import com.example.catalog_service.mapper.CategoryMapper;
import com.example.catalog_service.repository.CategoryRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
public class CategoryService {
    
    private final CategoryRepository categoryRepository;
    private final CategoryMapper categoryMapper;
    
    public CategoryService(CategoryRepository categoryRepository, CategoryMapper categoryMapper) {
        this.categoryRepository = categoryRepository;
        this.categoryMapper = categoryMapper;
    }
    
    @Transactional
    public CategoryResponse create(CategoryRequest request) {
        log.info("Creando categoría con código: {}", request.getCode());
        
        // Validar código duplicado
        if (categoryRepository.existsByCode(request.getCode())) {
            log.error("La categoría con código {} ya existe", request.getCode());
            throw new DuplicateResourceException("Ya existe una categoría con el código: " + request.getCode());
        }
        
        Category category = categoryMapper.toEntity(request);
        
        // Validar y asignar categoría padre si existe
        if (request.getParentCategoryId() != null) {
            Category parentCategory = categoryRepository.findById(request.getParentCategoryId())
                    .orElseThrow(() -> new ResourceNotFoundException(
                            "No se encontró la categoría padre con ID: " + request.getParentCategoryId()));
            category.setParentCategory(parentCategory);
        }
        
        Category savedCategory = categoryRepository.save(category);
        log.info("Categoría creada exitosamente con ID: {}", savedCategory.getId());
        
        return categoryMapper.toResponse(savedCategory);
    }
    
    @Transactional(readOnly = true)
    public List<CategoryResponse> findAll() {
        log.info("Obteniendo todas las categorías");
        List<Category> categories = categoryRepository.findAll();
        return categories.stream()
                .map(categoryMapper::toResponse)
                .collect(Collectors.toList());
    }
    
    @Transactional(readOnly = true)
    public CategoryResponse findById(Long id) {
        log.info("Buscando categoría con ID: {}", id);
        Category category = categoryRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("No se encontró la categoría con ID: " + id));
        return categoryMapper.toResponse(category);
    }
    
    @Transactional(readOnly = true)
    public CategoryResponse findByCode(String code) {
        log.info("Buscando categoría con código: {}", code);
        Category category = categoryRepository.findByCode(code)
                .orElseThrow(() -> new ResourceNotFoundException("No se encontró la categoría con código: " + code));
        return categoryMapper.toResponse(category);
    }
    
    @Transactional(readOnly = true)
    public List<CategoryResponse> findActive() {
        log.info("Obteniendo categorías activas");
        List<Category> categories = categoryRepository.findByIsActiveTrue();
        return categories.stream()
                .map(categoryMapper::toResponse)
                .collect(Collectors.toList());
    }
    
    @Transactional(readOnly = true)
    public List<CategoryResponse> search(String keyword) {
        log.info("Buscando categorías con palabra clave: {}", keyword);
        List<Category> categories = categoryRepository.searchByKeyword(keyword);
        return categories.stream()
                .map(categoryMapper::toResponse)
                .collect(Collectors.toList());
    }
    
    @Transactional
    public CategoryResponse update(Long id, CategoryRequest request) {
        log.info("Actualizando categoría con ID: {}", id);
        
        Category category = categoryRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("No se encontró la categoría con ID: " + id));
        
        // Validar código duplicado (excluyendo el actual)
        if (categoryRepository.existsByCodeAndIdNot(request.getCode(), id)) {
            log.error("Ya existe otra categoría con el código: {}", request.getCode());
            throw new DuplicateResourceException("Ya existe otra categoría con el código: " + request.getCode());
        }
        
        categoryMapper.updateEntity(category, request);
        
        // Actualizar categoría padre
        if (request.getParentCategoryId() != null) {
            if (request.getParentCategoryId().equals(id)) {
                throw new IllegalArgumentException("Una categoría no puede ser su propia categoría padre");
            }
            Category parentCategory = categoryRepository.findById(request.getParentCategoryId())
                    .orElseThrow(() -> new ResourceNotFoundException(
                            "No se encontró la categoría padre con ID: " + request.getParentCategoryId()));
            category.setParentCategory(parentCategory);
        } else {
            category.setParentCategory(null);
        }
        
        Category updatedCategory = categoryRepository.save(category);
        log.info("Categoría actualizada exitosamente con ID: {}", updatedCategory.getId());
        
        return categoryMapper.toResponse(updatedCategory);
    }
    
    @Transactional
    public void delete(Long id) {
        log.info("Eliminando categoría con ID: {}", id);
        
        Category category = categoryRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("No se encontró la categoría con ID: " + id));
        
        categoryRepository.delete(category);
        log.info("Categoría eliminada exitosamente con ID: {}", id);
    }
}
