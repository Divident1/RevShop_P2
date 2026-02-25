package com.revshop.service;

import com.revshop.dto.ProductRequest;
import com.revshop.dto.ProductUpdateRequest;
import com.revshop.dto.ThresholdRequest;
import com.revshop.model.Product;

import java.util.List;

public interface ProductService {

    Product addProduct(ProductRequest request,Long loggedInSellerId);

    Product updateProduct(Long id, ProductUpdateRequest request,Long loggedInSellerId);

    void deleteProduct(Long id,Long loggedInSellerId);

    Product setStockThreshold(Long productId, ThresholdRequest request,Long loggedInSellerId);

    List<Product> getAllProducts();
}