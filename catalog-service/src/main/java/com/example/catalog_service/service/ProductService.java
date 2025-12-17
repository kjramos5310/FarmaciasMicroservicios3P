package com.example.catalog_service.service;

import com.example.catalog_service.dto.request.ProductRequest;
import com.example.catalog_service.dto.response.ProductResponse;
import com.example.catalog_service.entity.Category;
import com.example.catalog_service.entity.Laboratory;
import com.example.catalog_service.entity.Product;
import com.example.catalog_service.exception.DuplicateResourceException;
import com.example.catalog_service.exception.ResourceNotFoundException;
import com.example.catalog_service.mapper.ProductMapper;
import com.example.catalog_service.repository.CategoryRepository;
import com.example.catalog_service.repository.LaboratoryRepository;
import com.example.catalog_service.repository.ProductRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
public class ProductService {
    
    private final ProductRepository productRepository;
    private final CategoryRepository categoryRepository;
    private final LaboratoryRepository laboratoryRepository;
    private final ProductMapper productMapper;
    
    public ProductService(ProductRepository productRepository, 
                         CategoryRepository categoryRepository,
                         LaboratoryRepository laboratoryRepository,
                         ProductMapper productMapper) {
        this.productRepository = productRepository;
        this.categoryRepository = categoryRepository;
        this.laboratoryRepository = laboratoryRepository;
        this.productMapper = productMapper;
    }
    
    @Transactional
    public ProductResponse create(ProductRequest request) {
        log.info("Creando producto con código: {}", request.getCode());
        
        // Validar código duplicado
        if (productRepository.existsByCode(request.getCode())) {
            log.error("El producto con código {} ya existe", request.getCode());
            throw new DuplicateResourceException("Ya existe un producto con el código: " + request.getCode());
        }
        
        // Validar código de barras duplicado
        if (productRepository.existsByBarcode(request.getBarcode())) {
            log.error("El producto con código de barras {} ya existe", request.getBarcode());
            throw new DuplicateResourceException("Ya existe un producto con el código de barras: " + request.getBarcode());
        }
        
        // Validar existencia de categoría
        Category category = categoryRepository.findById(request.getCategoryId())
                .orElseThrow(() -> new ResourceNotFoundException(
                        "No se encontró la categoría con ID: " + request.getCategoryId()));
        
        if (!category.getIsActive()) {
            throw new IllegalArgumentException("La categoría seleccionada no está activa");
        }
        
        // Validar existencia de laboratorio
        Laboratory laboratory = laboratoryRepository.findById(request.getLaboratoryId())
                .orElseThrow(() -> new ResourceNotFoundException(
                        "No se encontró el laboratorio con ID: " + request.getLaboratoryId()));
        
        if (!laboratory.getIsActive()) {
            throw new IllegalArgumentException("El laboratorio seleccionado no está activo");
        }
        
        Product product = productMapper.toEntity(request);
        product.setCategory(category);
        product.setLaboratory(laboratory);
        
        Product savedProduct = productRepository.save(product);
        log.info("Producto creado exitosamente con ID: {}", savedProduct.getId());
        
        return productMapper.toResponse(savedProduct);
    }
    
    @Transactional(readOnly = true)
    public List<ProductResponse> findAll() {
        log.info("Obteniendo todos los productos");
        List<Product> products = productRepository.findAll();
        return products.stream()
                .map(productMapper::toResponse)
                .collect(Collectors.toList());
    }
    
    @Transactional(readOnly = true)
    public ProductResponse findById(Long id) {
        log.info("Buscando producto con ID: {}", id);
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("No se encontró el producto con ID: " + id));
        return productMapper.toResponse(product);
    }
    
    @Transactional(readOnly = true)
    public ProductResponse findByCode(String code) {
        log.info("Buscando producto con código: {}", code);
        Product product = productRepository.findByCode(code)
                .orElseThrow(() -> new ResourceNotFoundException("No se encontró el producto con código: " + code));
        return productMapper.toResponse(product);
    }
    
    @Transactional(readOnly = true)
    public List<ProductResponse> search(String keyword) {
        log.info("Buscando productos con palabra clave: {}", keyword);
        List<Product> products = productRepository.searchByKeyword(keyword);
        return products.stream()
                .map(productMapper::toResponse)
                .collect(Collectors.toList());
    }
    
    @Transactional
    public ProductResponse update(Long id, ProductRequest request) {
        log.info("Actualizando producto con ID: {}", id);
        
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("No se encontró el producto con ID: " + id));
        
        // Validar código duplicado (excluyendo el actual)
        if (productRepository.existsByCodeAndIdNot(request.getCode(), id)) {
            log.error("Ya existe otro producto con el código: {}", request.getCode());
            throw new DuplicateResourceException("Ya existe otro producto con el código: " + request.getCode());
        }
        
        // Validar código de barras duplicado (excluyendo el actual)
        if (productRepository.existsByBarcodeAndIdNot(request.getBarcode(), id)) {
            log.error("Ya existe otro producto con el código de barras: {}", request.getBarcode());
            throw new DuplicateResourceException("Ya existe otro producto con el código de barras: " + request.getBarcode());
        }
        
        // Validar existencia de categoría
        Category category = categoryRepository.findById(request.getCategoryId())
                .orElseThrow(() -> new ResourceNotFoundException(
                        "No se encontró la categoría con ID: " + request.getCategoryId()));
        
        if (!category.getIsActive()) {
            throw new IllegalArgumentException("La categoría seleccionada no está activa");
        }
        
        // Validar existencia de laboratorio
        Laboratory laboratory = laboratoryRepository.findById(request.getLaboratoryId())
                .orElseThrow(() -> new ResourceNotFoundException(
                        "No se encontró el laboratorio con ID: " + request.getLaboratoryId()));
        
        if (!laboratory.getIsActive()) {
            throw new IllegalArgumentException("El laboratorio seleccionado no está activo");
        }
        
        productMapper.updateEntity(product, request);
        product.setCategory(category);
        product.setLaboratory(laboratory);
        
        Product updatedProduct = productRepository.save(product);
        log.info("Producto actualizado exitosamente con ID: {}", updatedProduct.getId());
        
        return productMapper.toResponse(updatedProduct);
    }
    
    @Transactional
    public void delete(Long id) {
        log.info("Eliminando producto con ID: {}", id);
        
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("No se encontró el producto con ID: " + id));
        
        productRepository.delete(product);
        log.info("Producto eliminado exitosamente con ID: {}", id);
    }
}
