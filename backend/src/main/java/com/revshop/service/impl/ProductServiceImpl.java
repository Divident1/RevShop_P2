package com.revshop.service.impl;

import com.revshop.dto.*;
import com.revshop.exception.ForbiddenException;
import com.revshop.exception.ResourceNotFoundException;
import com.revshop.model.Product;
import com.revshop.model.User;
import com.revshop.repository.ProductRepository;
import com.revshop.repository.UserRepository;
import com.revshop.service.ProductService;
import com.revshop.util.ProductMapper;

import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class ProductServiceImpl implements ProductService {

    private final ProductRepository productRepository;
    private final UserRepository userRepository;

    public ProductServiceImpl(ProductRepository productRepository, UserRepository userRepository) {
        this.productRepository = productRepository;
        this.userRepository = userRepository;
    }

    // ══════════════════════════════════════════════════════════════════════
    // Seller Methods
    // ══════════════════════════════════════════════════════════════════════

    public Product addProduct(ProductRequest request) {

        Long sellerId = resolveSellerId(request.getSellerId());
        validatePrice(request.getPrice(), request.getMrp());

        User seller = findUserById(sellerId);

        Product product = new Product();
        product.setName(request.getName());
        product.setDescription(request.getDescription());
        product.setPrice(request.getPrice());
        product.setMrp(request.getMrp());
        product.setCategoryName(request.getCategory());
        product.setQuantity(request.getQuantity());
        product.setSeller(seller);
        product.setStockThreshold(5);
        product.setDiscountPercentage(calculateDiscount(request.getPrice(), request.getMrp()));

        return productRepository.save(product);
    }

    public Product updateProduct(Long id, ProductUpdateRequest request) {

        Long sellerId = resolveSellerId(request.getSellerId());
        Product product = findProductById(id);

        validateOwnership(product, sellerId);
        validatePrice(request.getPrice(), request.getMrp());

        product.setName(request.getName());
        product.setDescription(request.getDescription());
        product.setPrice(request.getPrice());
        product.setMrp(request.getMrp());
        product.setCategoryName(request.getCategory());
        product.setQuantity(request.getQuantity());
        product.setDiscountPercentage(calculateDiscount(request.getPrice(), request.getMrp()));

        return productRepository.save(product);
    }

    public void deleteProduct(Long id, Long sellerIdFromRequest) {

        Long sellerId = resolveSellerId(sellerIdFromRequest);
        Product product = findProductById(id);

        validateOwnership(product, sellerId);

        product.setActive(false); // soft delete
        productRepository.save(product);
    }

    @Override
    public Product setStockThreshold(Long productId, ThresholdRequest request) {

        Long sellerId = resolveSellerId(request.getSellerId());
        Product product = findProductById(productId);

        validateOwnership(product, sellerId);

        product.setStockThreshold(request.getThreshold());
        return productRepository.save(product);
    }

    public List<Product> getAllProducts() {
        return productRepository.findAll();
    }

    // ══════════════════════════════════════════════════════════════════════
    // Buyer Methods
    // ══════════════════════════════════════════════════════════════════════

    @Override
    public Page<ProductDto> getProductsByCategory(Long categoryId, int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        return productRepository
                .findByCategory_IdAndActiveTrue(categoryId, pageable)
                .map(ProductMapper::mapToDto);
    }

    @Override
    public List<ProductDto> searchProducts(String keyword) {
        return productRepository
                .searchProducts(keyword)
                .stream()
                .map(ProductMapper::mapToDto)
                .collect(Collectors.toList());
    }

    @Override
    public ProductDto getProductDetails(Long productId) {
        Product product = findProductById(productId);
        return ProductMapper.mapToDto(product);
    }

    // ══════════════════════════════════════════════════════════════════════
    // DRY Helpers
    // ══════════════════════════════════════════════════════════════════════

    private User findUserById(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + userId));
    }

    private Product findProductById(Long productId) {
        return productRepository.findById(productId)
                .orElseThrow(() -> new ResourceNotFoundException("Product not found with id: " + productId));
    }

    private void validatePrice(Double price, Double mrp) {
        if (price > mrp) {
            throw new IllegalArgumentException("Selling price cannot exceed MRP");
        }
    }

    private void validateOwnership(Product product, Long sellerId) {
        if (product.getSeller() != null && !product.getSeller().getId().equals(sellerId)) {
            throw new ForbiddenException("You are not authorized to modify this product");
        }
    }

    private double calculateDiscount(Double price, Double mrp) {
        if (mrp == null || mrp == 0)
            return 0.0;
        return ((mrp - price) / mrp) * 100;
    }

    private Long resolveSellerId(Long fallbackSellerId) {
        if (fallbackSellerId != null) {
            return fallbackSellerId;
        }
        // TODO: Replace with JWT auth when implemented properly
        return 1L;
    }
}
