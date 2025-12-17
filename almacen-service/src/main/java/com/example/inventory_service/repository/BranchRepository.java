package com.example.inventory_service.repository;

import com.example.inventory_service.entity.Branch;
import com.example.inventory_service.entity.enums.BranchStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface BranchRepository extends JpaRepository<Branch, Long> {
    
    Optional<Branch> findByCode(String code);
    
    List<Branch> findByStatus(BranchStatus status);
    
    boolean existsByCode(String code);
}
