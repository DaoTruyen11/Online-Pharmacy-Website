package com.app.MediQuirk.repository;

import com.app.MediQuirk.model.ProductReview;
import org.springframework.data.domain.Page;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;


import java.util.List;

public interface ProductReviewRepository extends JpaRepository<ProductReview, Long> {
    Page<ProductReview> findByProduct_ProductId(Long productId, Pageable pageable);
    List<ProductReview> findByUser_UserId(Long userId);
    long countByProduct_ProductId(Long productId);
}
