package com.revshop.service.impl;

import com.revshop.dto.*;
import com.revshop.exception.ForbiddenException;
import com.revshop.exception.ResourceNotFoundException;
import com.revshop.model.Product;
import com.revshop.repository.ProductRepository;
import com.revshop.service.ProductService;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class ProductServiceImpl implements ProductService {

    private static final Logger logger = LogManager.getLogger(ProductServiceImpl.class);

    @Autowired
    private ProductRepository productRepository;

    // ══════════════════════════════════════════════════════════════════════
    // Kavya's Seller Methods
    // ══════════════════════════════════════════════════════════════════════

    public Product addProduct(ProductRequest request) {

        if (request.getPrice() > request.getMrp()) {
            throw new IllegalArgumentException("Price cannot exceed MRP");
        }

        Product product = new Product();
        product.setName(request.getName());
        product.setDescription(request.getDescription());
        product.setPrice(request.getPrice());
        product.setMrp(request.getMrp());
        product.setCategoryName(request.getCategory());
        product.setQuantity(request.getQuantity());

        double discount = ((request.getMrp() - request.getPrice()) / request.getMrp()) * 100;
        product.setDiscountPercentage(discount);

        Long sellerId = getCurrentUserId();
        logger.info("Seller {} adding product: {}", sellerId, request.getName());

        return productRepository.save(product);
    }

    public Product updateProduct(Long id, ProductUpdateRequest request) {

        Product product = productRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Product not found"));

        Long sellerId = getCurrentUserId();

        if (!product.getSellerId().equals(sellerId)) {
            throw new ForbiddenException("You are not the owner of this product");
        }

        if (request.getPrice() > request.getMrp()) {
            throw new IllegalArgumentException("Price cannot exceed MRP");
        }

        product.setName(request.getName());
        product.setDescription(request.getDescription());
        product.setPrice(request.getPrice());
        product.setMrp(request.getMrp());
        product.setCategoryName(request.getCategory());
        product.setQuantity(request.getQuantity());

        double discount = ((request.getMrp() - request.getPrice()) / request.getMrp()) * 100;
        product.setDiscountPercentage(discount);

        checkLowStock(product);

        return productRepository.save(product);
    }

    public void deleteProduct(Long id) {

        Product product = productRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Product not found"));

        Long sellerId = getCurrentUserId();

        if (!product.getSellerId().equals(sellerId)) {
            throw new ForbiddenException("You are not the owner of this product");
        }

        product.setIsActive(false); // soft delete
        productRepository.save(product);
    }

    @Override
    public Product setStockThreshold(Long productId, ThresholdRequest request) {

        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new ResourceNotFoundException("Product not found"));

        Long sellerId = getCurrentUserId();

        if (!product.getSellerId().equals(sellerId)) {
            throw new ForbiddenException("Only product owner can set threshold");
        }

        product.setStockThreshold(request.getThreshold());
        checkLowStock(product);

        return productRepository.save(product);
    }

    public List<Product> getAllProducts() {
        return productRepository.findAll();
    }

    // ══════════════════════════════════════════════════════════════════════
    // Jatin's Buyer Methods
    // ══════════════════════════════════════════════════════════════════════

    private ProductDto map(Product p) {
        return new ProductDto(
                p.getId(),
                p.getName(),
                p.getDescription(),
                p.getPrice(),
                p.getQuantity(),
                p.getRating());
    }

    @Override
    public Page<ProductDto> getProductsByCategory(Long categoryId, int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        return productRepository
                .findByCategoryIdAndIsActiveTrue(categoryId, pageable)
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
                .orElseThrow(() -> new ResourceNotFoundException("Product not found"));
        return map(product);
    }

    // ══════════════════════════════════════════════════════════════════════
    // Helpers
    // ══════════════════════════════════════════════════════════════════════

    private void checkLowStock(Product product) {
        if (product.getQuantity() <= product.getStockThreshold()) {
            logger.warn("LOW STOCK ALERT -> Product: {} | Stock: {}",
                    product.getName(),
                    product.getQuantity());
        }
    }

    private Long getCurrentUserId() {
        // TODO: Replace with real JWT auth
        return 1L;
    }
}