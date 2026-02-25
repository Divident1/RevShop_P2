package com.revshop.service.impl;

import com.revshop.dto.ProductRequest;
import com.revshop.dto.ProductUpdateRequest;
import com.revshop.dto.ThresholdRequest;
import com.revshop.exception.ForbiddenException;
import com.revshop.exception.ResourceNotFoundException;
import com.revshop.model.Product;
import com.revshop.repository.ProductRepository;
import com.revshop.service.ProductService;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import com.revshop.security.CustomerUserDetails;

import java.util.List;

@Service
public class ProductServiceImpl implements ProductService {

    private static final Logger logger = LogManager.getLogger(ProductServiceImpl.class);

    @Autowired
    private ProductRepository productRepository;

    public Product addProduct(ProductRequest request,Long loggedInSellerId) {

        if (request.getPrice() > request.getMrp()) {
            throw new IllegalArgumentException("Price cannot exceed MRP");
        }

        Product product = new Product();
        product.setName(request.getName());
        product.setDescription(request.getDescription());
        product.setPrice(request.getPrice());
        product.setMrp(request.getMrp());
        product.setCategory(request.getCategory());
        product.setQuantity(request.getQuantity());
        product.setSellerId(request.getSellerId());
        product.setStockThreshold(5);

        double discount = ((request.getMrp() - request.getPrice()) / request.getMrp()) * 100;
        product.setDiscountPercentage(discount);

        logger.info("Seller {} adding product: {}",loggedInSellerId, request.getName());

        return productRepository.save(product);
    }

    public Product updateProduct(Long id, ProductUpdateRequest request, Long loggedInSellerId) {

        Product product = productRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Product not found"));

        //Long sellerId = getCurrentUserId();

        if (!product.getSellerId().equals(loggedInSellerId)) {
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
    public void deleteProduct(Long id,Long loggedInSellerId) {

        Product product = productRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Product not found"));


        if (loggedInSellerId!=null && !product.getSellerId().equals(loggedInSellerId)) {
            throw new ForbiddenException("You are not the owner of this product");
        }

        product.setIsActive(false);   // soft delete
        productRepository.delete(product);
    }

    @Override
    public Product setStockThreshold(Long productId, ThresholdRequest request,Long loggedInSellerId) {

        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new ResourceNotFoundException("Product not found"));

        //Long sellerId = getCurrentUserId();

        // owner check
        if (!product.getSellerId().equals(loggedInSellerId)){
            throw new ForbiddenException("Only product owner can set threshold");
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

    public List<Product> getAllProducts() {

        return productRepository.findAll();
    }
}