package com.revshop.service.impl;

import com.revshop.dto.*;
import com.revshop.model.*;
import com.revshop.repository.*;
import com.revshop.service.SellerProductService;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class SellerProductServiceImpl implements SellerProductService {

    private final ProductRepository productRepository;
    private final UserRepository userRepository;

    public SellerProductServiceImpl(ProductRepository productRepository,
                                    UserRepository userRepository) {
        this.productRepository = productRepository;
        this.userRepository = userRepository;
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
    public String addProduct(SellerProductRequest req) {

        User seller = userRepository.findById(req.getSellerId())
                .orElseThrow(() -> new RuntimeException("Seller not found"));

        Product p = new Product();
        p.setName(req.getName());
        p.setDescription(req.getDescription());
        p.setPrice(req.getPrice());
        p.setQuantity(req.getQuantity());
        p.setActive(true);
        p.setSeller(seller);

        productRepository.save(p);

        return "Product added successfully";
    }

    @Override
    public String updateProduct(Long id, SellerProductRequest req) {

        Product p = productRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Product not found"));

        p.setName(req.getName());
        p.setDescription(req.getDescription());
        p.setPrice(req.getPrice());
        p.setQuantity(req.getQuantity());

        productRepository.save(p);

        return "Product updated";
    }

    @Override
    public String deleteProduct(Long id) {

        Product p = productRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Product not found"));

        p.setActive(false); // soft delete
        productRepository.save(p);

        return "Product deleted";
    }

    @Override
    public List<ProductDto> getSellerProducts(Long sellerId) {

        return productRepository.findBySellerId(sellerId)
                .stream()
                .map(this::map)
                .collect(Collectors.toList());
    }
}