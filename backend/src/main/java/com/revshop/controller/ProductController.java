package com.revshop.controller;

import com.revshop.dto.ProductRequest;
import com.revshop.dto.ProductUpdateRequest;
import com.revshop.dto.ThresholdRequest;
import com.revshop.model.Product;
import com.revshop.service.ProductService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/products")
public class ProductController {

    @Autowired
    private ProductService productService;

    @PostMapping
    public ResponseEntity<Product> addProduct(
            @Valid @RequestBody ProductRequest request) {

        Product product = productService.addProduct(request);
        return new ResponseEntity<>(product, HttpStatus.CREATED);
    }
    // UPDATE PRODUCT
    @PutMapping("/{id}")
    public ResponseEntity<Product> updateProduct(
            @PathVariable Long id,
            @Valid @RequestBody ProductUpdateRequest request) {

        Product updated = productService.updateProduct(id, request);
        return ResponseEntity.ok(updated); // 200
    }

    // SOFT DELETE PRODUCT
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteProduct(
            @PathVariable Long id) {

        productService.deleteProduct(id);
        return ResponseEntity.noContent().build(); // 204
    }

    @PutMapping("/{id}/threshold")
    public ResponseEntity<Product> setThreshold(
            @PathVariable Long id,
            @Valid @RequestBody ThresholdRequest request) {

        Product updated = productService.setStockThreshold(id, request);
        return ResponseEntity.ok(updated);
    }
}