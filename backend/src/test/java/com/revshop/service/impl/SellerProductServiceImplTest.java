package com.revshop.service.impl;

import com.revshop.dto.SellerProductRequest;
import com.revshop.dto.ProductDto;
import com.revshop.exception.ResourceNotFoundException;
import com.revshop.model.Product;
import com.revshop.model.Role;
import com.revshop.model.User;
import com.revshop.repository.ProductRepository;
import com.revshop.repository.UserRepository;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class SellerProductServiceImplTest {

    @Mock
    private ProductRepository productRepository;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private SellerProductServiceImpl sellerProductService;

    private User seller;
    private Product product;
    private SellerProductRequest request;

    @BeforeEach
    void setUp() {
        seller = new User();
        seller.setId(2L);
        seller.setName("Seller");
        seller.setRole(Role.SELLER);

        product = new Product();
        product.setId(100L);
        product.setName("Existing Product");
        product.setDescription("Existing Desc");
        product.setPrice(1000.0);
        product.setQuantity(5);
        product.setActive(true);
        product.setSeller(seller);

        request = new SellerProductRequest();
        request.setName("New Phone");
        request.setDescription("Good Phone");
        request.setPrice(20000.0);
        request.setQuantity(10);
        request.setSellerId(2L);
    }

    @Test
    @DisplayName("AddProduct - should add product successfully")
    void addProduct_Success() {
        when(userRepository.findById(2L)).thenReturn(Optional.of(seller));
        when(productRepository.save(any(Product.class))).thenReturn(product);

        String result = sellerProductService.addProduct(request);

        assertEquals("Product added successfully", result);
        verify(productRepository, times(1)).save(any(Product.class));
    }

    @Test
    @DisplayName("AddProduct - should throw when seller not found")
    void addProduct_SellerNotFound() {
        when(userRepository.findById(999L)).thenReturn(Optional.empty());

        request.setSellerId(999L);

        Exception exception = assertThrows(ResourceNotFoundException.class,
                () -> sellerProductService.addProduct(request));

        assertTrue(exception.getMessage().contains("Seller not found"));
        verify(productRepository, never()).save(any(Product.class));
    }

    @Test
    @DisplayName("UpdateProduct - should update successfully")
    void updateProduct_Success() {
        when(productRepository.findById(100L)).thenReturn(Optional.of(product));
        when(productRepository.save(any(Product.class))).thenReturn(product);

        String result = sellerProductService.updateProduct(100L, request);

        assertEquals("Product updated", result);
        assertEquals("New Phone", product.getName());
        assertEquals(20000.0, product.getPrice());
        verify(productRepository, times(1)).save(any(Product.class));
    }

    @Test
    @DisplayName("UpdateProduct - should throw when product not found")
    void updateProduct_ProductNotFound() {
        when(productRepository.findById(999L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class,
                () -> sellerProductService.updateProduct(999L, request));
    }

    @Test
    @DisplayName("DeleteProduct - should soft delete successfully")
    void deleteProduct_Success() {
        when(productRepository.findById(100L)).thenReturn(Optional.of(product));
        when(productRepository.save(any(Product.class))).thenReturn(product);

        String result = sellerProductService.deleteProduct(100L);

        assertEquals("Product deleted", result);
        assertFalse(product.isActive());
        verify(productRepository, times(1)).save(any(Product.class));
    }

    @Test
    @DisplayName("DeleteProduct - should throw when product not found")
    void deleteProduct_ProductNotFound() {
        when(productRepository.findById(999L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class,
                () -> sellerProductService.deleteProduct(999L));
    }

    @Test
    @DisplayName("GetSellerProducts - should return list of products")
    void getSellerProducts_Success() {
        when(productRepository.findBySeller_Id(2L)).thenReturn(List.of(product));

        List<ProductDto> products = sellerProductService.getSellerProducts(2L);

        assertEquals(1, products.size());
        assertEquals("Existing Product", products.get(0).getName());
    }

    @Test
    @DisplayName("GetSellerProducts - should return empty list if none")
    void getSellerProducts_Empty() {
        when(productRepository.findBySeller_Id(2L)).thenReturn(List.of());

        List<ProductDto> products = sellerProductService.getSellerProducts(2L);

        assertTrue(products.isEmpty());
    }
}
