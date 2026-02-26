package com.revshop.controller;

import com.revshop.dto.*;
import com.revshop.service.SellerProductService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@CrossOrigin(origins = "http://localhost:4200")
@RestController
@RequestMapping("/api/seller/products")
public class SellerProductController {

    private final SellerProductService service;

    public SellerProductController(SellerProductService service) {
        this.service = service;
    }

    @PostMapping
    public String addProduct(@RequestBody SellerProductRequest request) {
        return service.addProduct(request);
    }

    @PutMapping("/{id}")
    public String update(@PathVariable Long id,
                         @RequestBody SellerProductRequest request) {
        return service.updateProduct(id, request);
    }

    @DeleteMapping("/{id}")
    public String delete(@PathVariable Long id) {
        return service.deleteProduct(id);
    }

    @GetMapping("/{sellerId}")
    public List<ProductDto> getSellerProducts(@PathVariable Long sellerId) {
        return service.getSellerProducts(sellerId);
    }
}