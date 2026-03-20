package com.revshop.service.impl;

import com.revshop.dto.ProductRequest;
import com.revshop.dto.ProductUpdateRequest;
import com.revshop.dto.ThresholdRequest;
import com.revshop.dto.ProductDto;
import com.revshop.exception.ForbiddenException;
import com.revshop.exception.ResourceNotFoundException;
import com.revshop.model.Product;
import com.revshop.model.Role;
import com.revshop.model.User;
import com.revshop.repository.ProductRepository;
import com.revshop.repository.UserRepository;
import com.revshop.repository.CategoryRepository;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * Unit tests for ProductServiceImpl.
 */
@ExtendWith(MockitoExtension.class)
class ProductServiceImplTest {

    @Mock
    private ProductRepository productRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private CategoryRepository categoryRepository;

    @InjectMocks
    private ProductServiceImpl productService;

    private User seller;
    private Product product;

    @BeforeEach
    void setUp() {
        seller = new User();
        seller.setId(1L);
        seller.setName("TestSeller");
        seller.setEmail("seller@revshop.com");
        seller.setRole(Role.SELLER);
        seller.setBusinessName("Test Store");

        product = new Product();
        product.setId(1L);
        product.setName("Test Phone");
        product.setDescription("A great phone");
        product.setPrice(15000.0);
        product.setMrp(20000.0);
        product.setQuantity(50);
        product.setActive(true);
        product.setStockThreshold(5);
        product.setSeller(seller);
    }

    // --- Add Product Tests ---

    @Test
    @DisplayName("AddProduct - should add product successfully")
    void addProduct_Success() {
        ProductRequest request = new ProductRequest();
        request.setName("Test Phone");
        request.setDescription("A great phone");
        request.setPrice(15000.0);
        request.setMrp(20000.0);
        request.setQuantity(50);
        request.setCategory("Electronics");
        request.setSellerId(1L);

        when(userRepository.findById(1L)).thenReturn(Optional.of(seller));
        when(categoryRepository.findByName(anyString())).thenReturn(Optional.empty());
        when(categoryRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));
        when(productRepository.save(any(Product.class))).thenReturn(product);

        Product result = productService.addProduct(request);

        assertNotNull(result);
        assertEquals("Test Phone", result.getName());
        verify(productRepository, times(1)).save(any(Product.class));
    }

    @Test
    @DisplayName("AddProduct - should throw when price exceeds MRP")
    void addProduct_PriceExceedsMrp() {
        ProductRequest request = new ProductRequest();
        request.setPrice(25000.0);
        request.setMrp(20000.0);
        request.setSellerId(1L);

        assertThrows(IllegalArgumentException.class,
                () -> productService.addProduct(request));
    }

    @Test
    @DisplayName("AddProduct - should throw when seller not found")
    void addProduct_SellerNotFound() {
        ProductRequest request = new ProductRequest();
        request.setPrice(15000.0);
        request.setMrp(20000.0);
        request.setSellerId(999L);

        when(userRepository.findById(999L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class,
                () -> productService.addProduct(request));
    }

    // --- Update Product Tests ---

    @Test
    @DisplayName("UpdateProduct - should update product successfully")
    void updateProduct_Success() {
        ProductUpdateRequest request = new ProductUpdateRequest();
        request.setName("Updated Phone");
        request.setDescription("Updated description");
        request.setPrice(14000.0);
        request.setMrp(20000.0);
        request.setQuantity(45);
        request.setCategory("Electronics");
        request.setSellerId(1L);

        when(productRepository.findById(1L)).thenReturn(Optional.of(product));
        when(categoryRepository.findByName(anyString())).thenReturn(Optional.empty());
        when(categoryRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));
        when(productRepository.save(any(Product.class))).thenReturn(product);

        Product result = productService.updateProduct(1L, request);

        assertNotNull(result);
        verify(productRepository, times(1)).save(any(Product.class));
    }

    @Test
    @DisplayName("UpdateProduct - should throw when user is not the owner")
    void updateProduct_ForbiddenNotOwner() {
        ProductUpdateRequest request = new ProductUpdateRequest();
        request.setPrice(14000.0);
        request.setMrp(20000.0);
        request.setSellerId(999L); // different seller

        when(productRepository.findById(1L)).thenReturn(Optional.of(product));

        assertThrows(ForbiddenException.class,
                () -> productService.updateProduct(1L, request));
    }

    // --- Delete Product Tests ---

    @Test
    @DisplayName("DeleteProduct - should soft-delete product successfully")
    void deleteProduct_Success() {
        when(productRepository.findById(1L)).thenReturn(Optional.of(product));
        when(productRepository.save(any(Product.class))).thenReturn(product);

        productService.deleteProduct(1L, 1L);

        assertFalse(product.getActive());
        verify(productRepository, times(1)).save(product);
    }

    @Test
    @DisplayName("DeleteProduct - should throw when not product owner")
    void deleteProduct_ForbiddenNotOwner() {
        when(productRepository.findById(1L)).thenReturn(Optional.of(product));

        assertThrows(ForbiddenException.class,
                () -> productService.deleteProduct(1L, 999L));
    }

    // --- Get All Products Test ---

    @Test
    @DisplayName("GetAllProducts - should return all products")
    void getAllProducts_Success() {
        Product product2 = new Product();
        product2.setId(2L);
        product2.setName("Test Laptop");

        when(productRepository.findByActiveTrue()).thenReturn(Arrays.asList(product, product2));

        List<Product> results = productService.getAllProducts();

        assertEquals(2, results.size());
    }

    // --- Set Stock Threshold Tests ---

    @Test
    @DisplayName("SetStockThreshold - should update threshold successfully")
    void setStockThreshold_Success() {
        ThresholdRequest request = new ThresholdRequest();
        request.setThreshold(10);
        request.setSellerId(1L);

        when(productRepository.findById(1L)).thenReturn(Optional.of(product));
        when(productRepository.save(any(Product.class))).thenReturn(product);

        Product result = productService.setStockThreshold(1L, request);

        assertNotNull(result);
        verify(productRepository, times(1)).save(any(Product.class));
    }

    // --- Get Product Details Tests ---

    @Test
    @DisplayName("GetProductDetails - should return product DTO")
    void getProductDetails_Success() {
        when(productRepository.findById(1L)).thenReturn(Optional.of(product));

        ProductDto result = productService.getProductDetails(1L);

        assertNotNull(result);
        assertEquals("Test Phone", result.getName());
        assertEquals(15000.0, result.getPrice());
    }

    @Test
    @DisplayName("GetProductDetails - should throw when product not found")
    void getProductDetails_NotFound() {
        when(productRepository.findById(999L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class,
                () -> productService.getProductDetails(999L));
    }

    // --- Search Products Test ---

    @Test
    @DisplayName("SearchProducts - should return matching products")
    void searchProducts_Success() {
        when(productRepository.searchProducts("Phone")).thenReturn(List.of(product));

        List<ProductDto> results = productService.searchProducts("Phone");

        assertEquals(1, results.size());
        assertEquals("Test Phone", results.get(0).getName());
    }
}
