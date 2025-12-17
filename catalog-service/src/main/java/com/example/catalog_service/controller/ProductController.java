package com.example.catalog_service.controller;

import com.example.catalog_service.dto.request.ProductRequest;
import com.example.catalog_service.dto.response.ApiResponse;
import com.example.catalog_service.dto.response.ProductResponse;
import com.example.catalog_service.service.ProductService;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/products")
@CrossOrigin(origins = "*")
@Slf4j
public class ProductController {
    
    private final ProductService productService;
    
    public ProductController(ProductService productService) {
        this.productService = productService;
    }
    
    @PostMapping
    public ResponseEntity<ApiResponse<ProductResponse>> create(@Valid @RequestBody ProductRequest request) {
        log.info("Solicitud para crear producto: {}", request.getCode());
        ProductResponse response = productService.create(request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("Producto creado exitosamente", response));
    }
    
    @GetMapping
    public ResponseEntity<ApiResponse<List<ProductResponse>>> findAll() {
        log.info("Solicitud para obtener todos los productos");
        List<ProductResponse> responses = productService.findAll();
        return ResponseEntity.ok(ApiResponse.success("Productos obtenidos exitosamente", responses));
    }
    
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<ProductResponse>> findById(@PathVariable Long id) {
        log.info("Solicitud para obtener producto con ID: {}", id);
        ProductResponse response = productService.findById(id);
        return ResponseEntity.ok(ApiResponse.success("Producto obtenido exitosamente", response));
    }
    
    @GetMapping("/code/{code}")
    public ResponseEntity<ApiResponse<ProductResponse>> findByCode(@PathVariable String code) {
        log.info("Solicitud para obtener producto con código: {}", code);
        ProductResponse response = productService.findByCode(code);
        return ResponseEntity.ok(ApiResponse.success("Producto obtenido exitosamente", response));
    }
    
    @GetMapping("/search")
    public ResponseEntity<ApiResponse<List<ProductResponse>>> search(@RequestParam String keyword) {
        log.info("Solicitud para buscar productos con palabra clave: {}", keyword);
        List<ProductResponse> responses = productService.search(keyword);
        return ResponseEntity.ok(ApiResponse.success("Búsqueda realizada exitosamente", responses));
    }
    
    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<ProductResponse>> update(
            @PathVariable Long id,
            @Valid @RequestBody ProductRequest request) {
        log.info("Solicitud para actualizar producto con ID: {}", id);
        ProductResponse response = productService.update(id, request);
        return ResponseEntity.ok(ApiResponse.success("Producto actualizado exitosamente", response));
    }
    
    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> delete(@PathVariable Long id) {
        log.info("Solicitud para eliminar producto con ID: {}", id);
        productService.delete(id);
        return ResponseEntity.ok(ApiResponse.success("Producto eliminado exitosamente", null));
    }
}
