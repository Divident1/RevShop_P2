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

import java.util.List;

@RestController
@RequestMapping("/api/products")
@CrossOrigin(origins = "http://localhost:4200")
public class ProductController {

    @Autowired
    private ProductService productService;

    @PostMapping
    public ResponseEntity<Product> addProduct(
            @Valid @RequestBody ProductRequest request) {

        Product product = productService.addProduct(request, request.getSellerId());
        return new ResponseEntity<>(product, HttpStatus.CREATED);
    }
    // UPDATE PRODUCT
    @PutMapping("/{id}")
    public ResponseEntity<Product> updateProduct(
            @PathVariable Long id,
            @Valid @RequestBody ProductUpdateRequest request) {

        Product updated = productService.updateProduct(id, request,request.getSellerId());
        return ResponseEntity.ok(updated); // 200
    }

    // SOFT DELETE PRODUCT
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteProduct(
            @PathVariable Long id,
            @RequestParam(required = false) Long sellerId) {

        productService.deleteProduct(id,sellerId);
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/{id}/threshold")
    public ResponseEntity<Product> setThreshold(
            @PathVariable Long id,
            @Valid @RequestBody ThresholdRequest request) {

        Product updated = productService.setStockThreshold(id, request, request.getSellerId());
        return ResponseEntity.ok(updated);
    }

    @GetMapping
    public ResponseEntity<List<Product>> getAllProducts() {

        return ResponseEntity.ok(productService.getAllProducts());
    }
}