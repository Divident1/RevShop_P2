package com.revshop.service;

import com.revshop.dto.*;
import com.revshop.model.Product;
import org.springframework.data.domain.Page;

import java.util.List;

public interface ProductService {

    // Kavya's seller methods
    Product addProduct(ProductRequest request);

    Product updateProduct(Long id, ProductUpdateRequest request);

    void deleteProduct(Long id);

    Product setStockThreshold(Long productId, ThresholdRequest request);

    List<Product> getAllProducts();

    // Jatin's buyer methods
    Page<ProductDto> getProductsByCategory(Long categoryId, int page, int size);

    List<ProductDto> searchProducts(String keyword);

    ProductDto getProductDetails(Long productId);
}