package com.revshop.service;

import com.revshop.dto.ProductDto;
import com.revshop.dto.SellerProductRequest;

import java.util.List;

public interface SellerProductService {

    String addProduct(SellerProductRequest request);

    String updateProduct(Long productId, SellerProductRequest request);

    String deleteProduct(Long productId);

    List<ProductDto> getSellerProducts(Long sellerId);
}