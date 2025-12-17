package com.example.catalog_service.repository;

import com.example.catalog_service.entity.Laboratory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface LaboratoryRepository extends JpaRepository<Laboratory, Long> {
    
    Optional<Laboratory> findByName(String name);
    
    boolean existsByName(String name);
    
    boolean existsByNameAndIdNot(String name, Long id);
    
    List<Laboratory> findByIsActiveTrue();
    
    @Query("SELECT l FROM Laboratory l WHERE " +
           "LOWER(l.name) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
           "LOWER(l.country) LIKE LOWER(CONCAT('%', :keyword, '%'))")
    List<Laboratory> searchByKeyword(@Param("keyword") String keyword);
}
