package com.app.MediQuirk.services;

import com.app.MediQuirk.model.Product;
import com.app.MediQuirk.model.ProductReview;
import com.app.MediQuirk.model.Users;
import com.app.MediQuirk.repository.ProductRepository;
import com.app.MediQuirk.repository.ProductReviewRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class ProductReviewService {
    private final ProductReviewRepository reviewRepository;
    private final ProductRepository productRepository;

    public ProductReviewService(ProductReviewRepository reviewRepository, ProductRepository productRepository) {
        this.reviewRepository = reviewRepository;
        this.productRepository = productRepository;
    }

    public ProductReview addReview(Long productId, ProductReview review, Users user) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new RuntimeException("Product not found with id " + productId));

        review.setProduct(product);
        review.setUser(user);
        review.setReviewDate(LocalDateTime.now());

        return reviewRepository.save(review);
    }

    public Page<ProductReview> getReviewsByProductId(Long productId, Pageable pageable) {
        return reviewRepository.findByProduct_ProductId(productId, pageable);
    }

    public List<ProductReview> getReviewsByUser(Long userId) {
        return reviewRepository.findByUser_UserId(userId);
    }

    public ProductReview getReviewById(Long reviewId) {
        return reviewRepository.findById(reviewId)
                .orElseThrow(() -> new RuntimeException("Review not found"));
    }

    public ProductReview updateReview(ProductReview review) {
        ProductReview existingReview = getReviewById(review.getReviewId());
        existingReview.setRating(review.getRating());
        existingReview.setComment(review.getComment());
        existingReview.setReviewDate(LocalDateTime.now());
        return reviewRepository.save(existingReview);
    }

    public void deleteReview(Long reviewId) {
        reviewRepository.deleteById(reviewId);
    }

    public boolean isReviewOwnedByUser(Long reviewId, Users user) {
        Optional<ProductReview> review = reviewRepository.findById(reviewId);
        return review.map(r -> r.getUser().getUserId().equals(user.getUserId())).orElse(false);
    }
    public Page<ProductReview> getAllReviews(Pageable pageable) {
        return reviewRepository.findAll(pageable);
    }
    public double getAverageRatingForProduct(Long productId) {
        Pageable unpaged = Pageable.unpaged();
        Page<ProductReview> reviewPage = reviewRepository.findByProduct_ProductId(productId, unpaged);
        List<ProductReview> reviews = reviewPage.getContent();
        if (reviews.isEmpty()) {
            return 0.0;
        }
        double sum = reviews.stream().mapToInt(ProductReview::getRating).sum();
        return sum / reviews.size();
    }

    public long getTotalReviewsForProduct(Long productId) {
        return reviewRepository.countByProduct_ProductId(productId);
    }
}