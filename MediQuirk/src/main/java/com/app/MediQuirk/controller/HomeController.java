package com.app.MediQuirk.controller;

import com.app.MediQuirk.model.Category;
import com.app.MediQuirk.model.Product;
import com.app.MediQuirk.services.CategoryService;
import com.app.MediQuirk.services.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
public class HomeController {

    @Autowired
    private ProductService productService;

    @RequestMapping("/")
    public String HomePage(Model model,
                           @RequestParam(defaultValue = "0") int page,
                           @RequestParam(defaultValue = "30") int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<Product> productPage = productService.getAllProductsPaginated(pageable);

        model.addAttribute("products", productPage.getContent());
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", productPage.getTotalPages());
        model.addAttribute("totalItems", productPage.getTotalElements());
        model.addAttribute("size", size);  // Add this line
        return "User/index";
    }
    @Autowired
    private CategoryService categoryService;

    @ModelAttribute("categories")
    public List<Category> getCategories() {
        return categoryService.getAllCategories();
    }
    @GetMapping("/category/{categoryId}")
    public String showProductsByCategory(@PathVariable Long categoryId,
                                         @RequestParam(defaultValue = "0") int page,
                                         Model model) {
        int pageSize = 30;
        Page<Product> productPage = productService.getProductsByCategoryId(categoryId, PageRequest.of(page, pageSize));

        model.addAttribute("products", productPage.getContent());
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", productPage.getTotalPages());
        model.addAttribute("categoryId", categoryId);

        return "User/product/category-products";
    }
}
