package com.app.MediQuirk.repository;

import com.app.MediQuirk.model.Product;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {
    Page<Product> findByProductNameContainingIgnoreCase(String keyword, Pageable pageable);
    List<Product> findByProductNameContainingIgnoreCase(String keyword);
    Page<Product> findByCategoryCategoryId(Long categoryId, Pageable pageable);
}
