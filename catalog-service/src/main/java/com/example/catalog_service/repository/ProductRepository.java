package com.example.catalog_service.repository;

import com.example.catalog_service.entity.Product;
import com.example.catalog_service.entity.enums.ProductStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {
    
    Optional<Product> findByCode(String code);
    
    Optional<Product> findByBarcode(String barcode);
    
    boolean existsByCode(String code);
    
    boolean existsByBarcode(String barcode);
    
    boolean existsByCodeAndIdNot(String code, Long id);
    
    boolean existsByBarcodeAndIdNot(String barcode, Long id);
    
    List<Product> findByStatus(ProductStatus status);
    
    List<Product> findByCategoryId(Long categoryId);
    
    List<Product> findByLaboratoryId(Long laboratoryId);
    
    @Query("SELECT p FROM Product p WHERE " +
           "LOWER(p.name) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
           "LOWER(p.genericName) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
           "LOWER(p.code) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
           "LOWER(p.barcode) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
           "LOWER(p.activeIngredient) LIKE LOWER(CONCAT('%', :keyword, '%'))")
    List<Product> searchByKeyword(@Param("keyword") String keyword);
}
