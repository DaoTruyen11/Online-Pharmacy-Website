package com.app.MediQuirk.controller.User;

import com.app.MediQuirk.model.Product;
import com.app.MediQuirk.model.ProductReview;
import com.app.MediQuirk.model.Users;
import com.app.MediQuirk.services.*;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("products")
public class ProductDetailController {

    private final ProductService productService;
    private final CategoryService categoryService;
    private final SupplierService supplierService;
    private final ProductReviewService productReviewService;
    private final UserService userService;

    public ProductDetailController(ProductService productService,
                                   CategoryService categoryService,
                                   SupplierService supplierService,
                                   ProductReviewService productReviewService,
                                   UserService userService) {
        this.productService = productService;
        this.categoryService = categoryService;
        this.supplierService = supplierService;
        this.productReviewService = productReviewService;
        this.userService = userService;
    }



    @GetMapping("/detail/{id}")
    public String showProductDetail(@PathVariable Long id,
                                    @RequestParam(defaultValue = "0") int page,
                                    @RequestParam(defaultValue = "6") int size,
                                    Model model) {
        Product product = productService.getProductById(id)
                .orElseThrow(() -> new IllegalArgumentException("Invalid product Id:" + id));

        Page<ProductReview> reviewPage = productReviewService.getReviewsByProductId(id, PageRequest.of(page, size));

        // Tính trung bình đánh giá
        double averageRating = productReviewService.getAverageRatingForProduct(id);
        // Lấy tổng số đánh giá
        long totalReviews = productReviewService.getTotalReviewsForProduct(id);

        model.addAttribute("product", product);
        model.addAttribute("reviewPage", reviewPage);
        model.addAttribute("productReview", new ProductReview());
        model.addAttribute("categories", categoryService.getAllCategories());
        model.addAttribute("suppliers", supplierService.getAllSuppliers());
        model.addAttribute("averageRating", averageRating);
        model.addAttribute("totalReviews", totalReviews);

        return "/User/product/detail";
    }

    @PostMapping("/review/add")
    public String addReview(@RequestParam Long productId, @ModelAttribute ProductReview productReview) {
        Users currentUser = getCurrentUser();
        if (currentUser != null) {
            productReviewService.addReview(productId, productReview, currentUser);
        }
        return "redirect:/products/detail/" + productId;
    }

    @GetMapping("/review/edit/{id}")
    public String showEditReviewForm(@PathVariable Long id, Model model) {
        ProductReview review = productReviewService.getReviewById(id);
        if (review != null && getCurrentUser().getUsername().equals(review.getUser().getUsername())) {
            model.addAttribute("review", review);
            return "/User/product/edit-review";
        }
        return "redirect:/products/detail/" + review.getProduct().getProductId();
    }

    @PostMapping("/review/edit/{id}")
    public String updateReview(@PathVariable Long id, @ModelAttribute ProductReview updatedReview) {
        ProductReview existingReview = productReviewService.getReviewById(id);
        if (existingReview != null && getCurrentUser().getUsername().equals(existingReview.getUser().getUsername())) {
            existingReview.setRating(updatedReview.getRating());
            existingReview.setComment(updatedReview.getComment());
            productReviewService.updateReview(existingReview);
        }
        return "redirect:/products/detail/" + existingReview.getProduct().getProductId();
    }

    @PostMapping("/review/delete/{id}")
    public String deleteReview(@PathVariable Long id) {
        Users currentUser = getCurrentUser();
        ProductReview review = productReviewService.getReviewById(id);
        Long productId = review.getProduct().getProductId();
        if (currentUser != null && productReviewService.isReviewOwnedByUser(id, currentUser)) {
            productReviewService.deleteReview(id);
        }
        return "redirect:/products/detail/" + productId;
    }

    @GetMapping("/management")
    public String showProductManagement() {
        return "/api/product-api";
    }

    private Users getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String currentUsername = authentication.getName();
        return userService.findByUsername(currentUsername).orElse(null);
    }
}