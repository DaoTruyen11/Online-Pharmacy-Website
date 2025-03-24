package com.app.MediQuirk.controller.api;

import com.app.MediQuirk.model.Product;
import com.app.MediQuirk.services.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@CrossOrigin
@RestController
@RequestMapping("/api/products")
public class ProductApiController {

    @Autowired
    private ProductService productService;

    @GetMapping()
    public List<Product> getAllProducts() {
        return productService.getAllProducts();
    }

    @GetMapping("/all")
    public List<Product> getAllProductsIncludingOutOfStock() {
        return productService.getAllProductsIncludingOutOfStock();
    }

    @PostMapping
    public Product createProduct(@RequestPart("product") Product product,
                                 @RequestPart(value = "image", required = false) MultipartFile image) throws IOException {
        return productService.addProduct(product, image);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Product> getProductById(@PathVariable Long id) {
        Product product = productService.getProductById(id)
                .orElseThrow(() -> new RuntimeException("Product not found on :: " + id));
        return ResponseEntity.ok().body(product);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Product> updateProduct(@PathVariable Long id,
                                                 @RequestPart("product") Product productDetails,
                                                 @RequestPart(value = "image", required = false) MultipartFile image) throws IOException {
        Product updatedProduct = productService.updateProduct(id, productDetails, image);
        return ResponseEntity.ok(updatedProduct);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteProduct(@PathVariable Long id) {
        productService.deleteProductById(id);
        return ResponseEntity.ok().build();
    }


}