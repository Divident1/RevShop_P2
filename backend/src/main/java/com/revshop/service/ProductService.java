package com.revshop.service;

import com.revshop.dto.ProductDto;
import org.springframework.data.domain.Page;

import java.util.List;

public interface ProductService {

    Page<ProductDto> getProductsByCategory(Long categoryId,
                                           int page,
                                           int size);

    List<ProductDto> searchProducts(String keyword);

    ProductDto getProductDetails(Long productId);
}