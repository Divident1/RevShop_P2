package com.revshop.controller;

import com.revshop.dto.ProductDto;
import com.revshop.dto.ProductRequest;
import com.revshop.dto.ProductUpdateRequest;
import com.revshop.dto.ThresholdRequest;
import com.revshop.model.Product;
import com.revshop.service.ProductService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@CrossOrigin(origins = "*")
@RestController
@RequestMapping("/api/products")
public class ProductController {

    @Autowired
    private ProductService productService;

    // ── Kavya's Seller Endpoints ───────────────────────────────────────

    @PostMapping
    public ResponseEntity<Product> addProduct(
            @Valid @RequestBody ProductRequest request) {
        Product product = productService.addProduct(request);
        return new ResponseEntity<>(product, HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Product> updateProduct(
            @PathVariable Long id,
            @Valid @RequestBody ProductUpdateRequest request) {
        Product updated = productService.updateProduct(id, request);
        return ResponseEntity.ok(updated);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteProduct(
            @PathVariable Long id,
            @RequestParam(required = false) Long sellerId) {
        productService.deleteProduct(id, sellerId);
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/{id}/threshold")
    public ResponseEntity<Product> setThreshold(
            @PathVariable Long id,
            @Valid @RequestBody ThresholdRequest request) {
        Product updated = productService.setStockThreshold(id, request);
        return ResponseEntity.ok(updated);
    }

    @GetMapping
    public ResponseEntity<List<Product>> getAllProducts() {
        return ResponseEntity.ok(productService.getAllProducts());
    }

    // ── Jatin's Buyer Endpoints ────────────────────────────────────────

    @GetMapping("/category/{categoryId}")
    public Page<ProductDto> getByCategory(
            @PathVariable Long categoryId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "5") int size) {
        return productService.getProductsByCategory(categoryId, page, size);
    }

    @GetMapping("/search")
    public List<ProductDto> search(@RequestParam String keyword) {
        return productService.searchProducts(keyword);
    }

    @GetMapping("/details/{id}")
    public ProductDto getDetails(@PathVariable Long id) {
        return productService.getProductDetails(id);
    }
}
