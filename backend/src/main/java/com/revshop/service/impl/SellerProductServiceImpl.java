package com.revshop.service.impl;

import com.revshop.dto.*;
import com.revshop.exception.ResourceNotFoundException;
import com.revshop.model.*;
import com.revshop.repository.*;
import com.revshop.service.SellerProductService;
import com.revshop.util.ProductMapper;

import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class SellerProductServiceImpl implements SellerProductService {

    private final ProductRepository productRepository;
    private final UserRepository userRepository;

    public SellerProductServiceImpl(ProductRepository productRepository, UserRepository userRepository) {
        this.productRepository = productRepository;
        this.userRepository = userRepository;
    }

    @Override
    public String addProduct(SellerProductRequest request) {

        User seller = userRepository.findById(request.getSellerId())
                .orElseThrow(() -> new ResourceNotFoundException("Seller not found with id: " + request.getSellerId()));

        Product product = new Product();
        product.setName(request.getName());
        product.setDescription(request.getDescription());
        product.setPrice(request.getPrice());
        product.setQuantity(request.getQuantity());
        product.setActive(true);
        product.setSeller(seller);

        productRepository.save(product);

        return "Product added successfully";
    }

    @Override
    public String updateProduct(Long id, SellerProductRequest request) {

        Product product = findProductById(id);

        product.setName(request.getName());
        product.setDescription(request.getDescription());
        product.setPrice(request.getPrice());
        product.setQuantity(request.getQuantity());

        productRepository.save(product);

        return "Product updated";
    }

    @Override
    public String deleteProduct(Long id) {

        Product product = findProductById(id);
        product.setActive(false); // soft delete
        productRepository.save(product);

        return "Product deleted";
    }

    @Override
    public List<ProductDto> getSellerProducts(Long sellerId) {

        return productRepository.findBySeller_Id(sellerId)
                .stream()
                .map(ProductMapper::mapToDto) // Replaced identical private map method with DRY helper
                .collect(Collectors.toList());
    }

    // ── DRY Helpers ───────────────────────────────────────────────────

    private Product findProductById(Long productId) {
        return productRepository.findById(productId)
                .orElseThrow(() -> new ResourceNotFoundException("Product not found with id: " + productId));
    }
}