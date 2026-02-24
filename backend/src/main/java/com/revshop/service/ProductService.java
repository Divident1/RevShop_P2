package com.revshop.service;

import com.revshop.dto.ProductRequest;
import com.revshop.dto.ProductUpdateRequest;
import com.revshop.dto.ThresholdRequest;
import com.revshop.model.Product;

public interface ProductService {

    Product addProduct(ProductRequest request);

    Product updateProduct(Long id, ProductUpdateRequest request);

    void deleteProduct(Long id);

    Product setStockThreshold(Long productId, ThresholdRequest request);
}