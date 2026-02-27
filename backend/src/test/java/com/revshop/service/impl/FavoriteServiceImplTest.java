package com.revshop.service.impl;

import com.revshop.exception.DuplicateResourceException;
import com.revshop.exception.ResourceNotFoundException;
import com.revshop.model.Favorite;
import com.revshop.model.Product;
import com.revshop.model.Role;
import com.revshop.model.User;
import com.revshop.repository.FavoriteRepository;
import com.revshop.repository.ProductRepository;
import com.revshop.repository.UserRepository;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class FavoriteServiceImplTest {

    @Mock
    private FavoriteRepository favoriteRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private ProductRepository productRepository;

    @InjectMocks
    private FavoriteServiceImpl favoriteService;

    private User buyer;
    private Product product;
    private Favorite favorite;

    @BeforeEach
    void setUp() {
        buyer = new User();
        buyer.setId(1L);
        buyer.setName("Buyer");
        buyer.setRole(Role.BUYER);

        product = new Product();
        product.setId(1L);
        product.setName("Test Phone");
        product.setPrice(15000.0);

        favorite = new Favorite();
        favorite.setId(1L);
        favorite.setBuyer(buyer);
        favorite.setProduct(product);
        favorite.setAddedAt(LocalDateTime.now());
    }

    @Test
    @DisplayName("AddFavorite - should add favorite successfully")
    void addFavorite_Success() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(buyer));
        when(productRepository.findById(1L)).thenReturn(Optional.of(product));
        when(favoriteRepository.existsByBuyerIdAndProductId(1L, 1L)).thenReturn(false);
        when(favoriteRepository.save(any(Favorite.class))).thenReturn(favorite);

        Favorite result = favoriteService.addFavorite(1L, 1L);

        assertNotNull(result);
        assertEquals("Test Phone", result.getProduct().getName());
        verify(favoriteRepository, times(1)).save(any(Favorite.class));
    }

    @Test
    @DisplayName("AddFavorite - should throw when already favorited")
    void addFavorite_AlreadyFavorited() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(buyer));
        when(productRepository.findById(1L)).thenReturn(Optional.of(product));
        when(favoriteRepository.existsByBuyerIdAndProductId(1L, 1L)).thenReturn(true);

        Exception ex = assertThrows(DuplicateResourceException.class,
                () -> favoriteService.addFavorite(1L, 1L));

        assertTrue(ex.getMessage().contains("already in your favorites"));
    }

    @Test
    @DisplayName("AddFavorite - should throw when buyer not found")
    void addFavorite_BuyerNotFound() {
        when(userRepository.findById(999L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class,
                () -> favoriteService.addFavorite(999L, 1L));
    }

    @Test
    @DisplayName("AddFavorite - should throw when product not found")
    void addFavorite_ProductNotFound() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(buyer));
        when(productRepository.findById(999L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class,
                () -> favoriteService.addFavorite(1L, 999L));
    }

    @Test
    @DisplayName("RemoveFavorite - should remove favorite successfully")
    void removeFavorite_Success() {
        when(favoriteRepository.findByBuyerIdAndProductId(1L, 1L))
                .thenReturn(Optional.of(favorite));
        doNothing().when(favoriteRepository).delete(favorite);

        assertDoesNotThrow(() -> favoriteService.removeFavorite(1L, 1L));
        verify(favoriteRepository, times(1)).delete(favorite);
    }

    @Test
    @DisplayName("RemoveFavorite - should throw when favorite not found")
    void removeFavorite_NotFound() {
        when(favoriteRepository.findByBuyerIdAndProductId(1L, 999L))
                .thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class,
                () -> favoriteService.removeFavorite(1L, 999L));
    }

    @Test
    @DisplayName("GetFavoritesByBuyer - should return favorites list")
    void getFavoritesByBuyer_Success() {
        when(favoriteRepository.findByBuyerIdOrderByAddedAtDesc(1L))
                .thenReturn(List.of(favorite));

        List<Favorite> results = favoriteService.getFavoritesByBuyer(1L);

        assertEquals(1, results.size());
    }

    @Test
    @DisplayName("IsFavorite - should return true for existing favorite")
    void isFavorite_True() {
        when(favoriteRepository.existsByBuyerIdAndProductId(1L, 1L)).thenReturn(true);

        assertTrue(favoriteService.isFavorite(1L, 1L));
    }

    @Test
    @DisplayName("IsFavorite - should return false for non-existing favorite")
    void isFavorite_False() {
        when(favoriteRepository.existsByBuyerIdAndProductId(1L, 999L)).thenReturn(false);

        assertFalse(favoriteService.isFavorite(1L, 999L));
    }
}
