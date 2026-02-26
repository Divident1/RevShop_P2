package com.revshop.service.impl;

import com.revshop.dto.ProductRequest;
import com.revshop.dto.ProductUpdateRequest;
import com.revshop.dto.ThresholdRequest;
import com.revshop.exception.ForbiddenException;
import com.revshop.exception.ResourceNotFoundException;
import com.revshop.model.Product;
import com.revshop.model.User;
import com.revshop.repository.ProductRepository;
import com.revshop.service.ProductService;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import com.revshop.security.CustomerUserDetails;
import com.revshop.repository.UserRepository;

import java.util.List;

@Service
public class ProductServiceImpl implements ProductService {

    private static final Logger logger = LogManager.getLogger(ProductServiceImpl.class);

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private UserRepository userRepository;

    public Product addProduct(ProductRequest request) {

        Long sellerId = resolveSellerId(request.getSellerId());

        if (request.getPrice() > request.getMrp()) {
            throw new IllegalArgumentException("Price cannot exceed MRP");
        }

        User seller = userRepository.findById(sellerId)
                .orElseThrow(() -> new ResourceNotFoundException("Seller not found"));


        Product product = new Product();
        product.setName(request.getName());
        product.setDescription(request.getDescription());
        product.setPrice(request.getPrice());
        product.setMrp(request.getMrp());
        product.setCategory(request.getCategory());
        product.setQuantity(request.getQuantity());
        product.setSeller(seller);
        product.setStockThreshold(5);

        double discount = ((request.getMrp() - request.getPrice()) / request.getMrp()) * 100;
        product.setDiscountPercentage(discount);

        logger.info("Seller {} adding product: {}",sellerId, request.getName());

        return productRepository.save(product);
    }

    public Product updateProduct(Long id, ProductUpdateRequest request) {

        Long sellerId = resolveSellerId(request.getSellerId());

        Product product = productRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Product not found"));


        if (!product.getSeller().getId().equals(sellerId)) {
            throw new ForbiddenException("You are not the owner of this product");
        }

        if (request.getPrice() > request.getMrp()) {
            throw new IllegalArgumentException("Price cannot exceed MRP");
        }

        product.setName(request.getName());
        product.setDescription(request.getDescription());
        product.setPrice(request.getPrice());
        product.setMrp(request.getMrp());
        product.setCategory(request.getCategory());
        product.setQuantity(request.getQuantity());

        double discount = ((request.getMrp() - request.getPrice()) / request.getMrp()) * 100;
        product.setDiscountPercentage(discount);

        checkLowStock(product);

        return productRepository.save(product);
    }
    public void deleteProduct(Long id, Long sellerIdFromRequest) {

        Long sellerId = resolveSellerId(sellerIdFromRequest);

        Product product = productRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Product not found"));

        if (!product.getSeller().getId().equals(sellerId)) {
            throw new ForbiddenException("You are not the owner of this product");
        }

        product.setIsActive(false);   // soft delete
        productRepository.delete(product);
    }

    @Override
    public Product setStockThreshold(Long productId, ThresholdRequest request) {

        Long sellerId= getCurrentSellerId();

        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new ResourceNotFoundException("Product not found"));

        if (!product.getSeller().getId().equals(sellerId)) {
            throw new ForbiddenException("Only owner can set threshold");
        }

        product.setStockThreshold(request.getThreshold());

        // alert check
        checkLowStock(product);

        return productRepository.save(product);
    }

    private void checkLowStock(Product product) {
        if (product.getQuantity() <= product.getStockThreshold()) {
            logger.warn("LOW STOCK ALERT -> Product: {} | Stock: {}",
                    product.getName(),
                    product.getQuantity());
        }
    }

    private Long getCurrentSellerId() {
        return resolveSellerId(null);
    }

    private Long resolveSellerId(Long fallbackSellerId) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null && auth.getPrincipal() instanceof CustomerUserDetails user) {
            return user.getId();
        }

        if (fallbackSellerId != null) {
            return fallbackSellerId;
        }

        throw new ForbiddenException("Seller authentication required");
    }

    public List<Product> getAllProducts() {

        return productRepository.findAll();
    }
}
