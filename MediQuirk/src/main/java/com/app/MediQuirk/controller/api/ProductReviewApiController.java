//package com.app.MediQuirk.controller.api;
//
//import com.app.MediQuirk.model.ProductReview;
//import com.app.MediQuirk.services.ProductReviewService;
//import jakarta.validation.Valid;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.http.ResponseEntity;
//import org.springframework.web.bind.annotation.*;
//
//import java.util.List;
//
//@RestController
//@RequestMapping("/api/reviews")
//public class ProductReviewApiController {
//    @Autowired
//    private ProductReviewService reviewService;
//
//    @PostMapping
//    public ResponseEntity<ProductReview> addReview(@Valid @RequestBody ProductReview review) {
//        return ResponseEntity.ok(reviewService.addReview(review));
//    }
//
//    @GetMapping("/product/{productId}")
//    public ResponseEntity<List<ProductReview>> getReviewsByProduct(@PathVariable Long productId) {
//        return ResponseEntity.ok(reviewService.getReviewsByProduct(productId));
//    }
//
//    @GetMapping("/user/{userId}")
//    public ResponseEntity<List<ProductReview>> getReviewsByUser(@PathVariable Long userId) {
//        return ResponseEntity.ok(reviewService.getReviewsByUser(userId));
//    }
//}
