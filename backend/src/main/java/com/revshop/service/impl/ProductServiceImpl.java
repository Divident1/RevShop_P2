package com.revshop.service.impl;

import com.revshop.dto.ProductDto;
import com.revshop.model.Product;
import com.revshop.repository.ProductRepository;
import com.revshop.service.ProductService;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class ProductServiceImpl implements ProductService {

    private final ProductRepository productRepository;

    public ProductServiceImpl(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }

    private ProductDto map(Product p) {
        return new ProductDto(
                p.getId(),
                p.getName(),
                p.getDescription(),
                p.getPrice(),
                p.getQuantity(),
                p.getRating()
        );
    }

    @Override
    public Page<ProductDto> getProductsByCategory(Long categoryId,
                                                  int page,
                                                  int size) {

        Pageable pageable = PageRequest.of(page, size);

        return productRepository
                .findByCategoryIdAndActiveTrue(categoryId, pageable)
                .map(this::map);
    }

    @Override
    public List<ProductDto> searchProducts(String keyword) {

        return productRepository
                .searchProducts(keyword)
                .stream()
                .map(this::map)
                .collect(Collectors.toList());
    }

    @Override
    public ProductDto getProductDetails(Long productId) {

        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new RuntimeException("Product not found"));

        return map(product);
    }
}