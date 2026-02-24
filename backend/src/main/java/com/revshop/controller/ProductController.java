package com.revshop.controller;

import com.revshop.dto.ProductDto;
import com.revshop.service.ProductService;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@CrossOrigin(origins = "http://localhost:4200")
@RestController
@RequestMapping("/api/products")
public class ProductController {

    private final ProductService productService;

    public ProductController(ProductService productService) {
        this.productService = productService;
    }

    // Browse by Category
    @GetMapping("/category/{categoryId}")
    public Page<ProductDto> getByCategory(
            @PathVariable Long categoryId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "5") int size) {

        return productService.getProductsByCategory(categoryId, page, size);
    }

    // Search Products
    @GetMapping("/search")
    public List<ProductDto> search(
            @RequestParam String keyword) {

        return productService.searchProducts(keyword);
    }

    // Product Details
    @GetMapping("/{id}")
    public ProductDto getDetails(@PathVariable Long id) {
        return productService.getProductDetails(id);
    }
}