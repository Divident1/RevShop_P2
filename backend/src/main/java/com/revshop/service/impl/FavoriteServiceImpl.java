package com.revshop.service.impl;

import com.revshop.exception.DuplicateResourceException;
import com.revshop.exception.ResourceNotFoundException;
import com.revshop.model.Favorite;
import com.revshop.model.Product;
import com.revshop.model.User;
import com.revshop.repository.FavoriteRepository;
import com.revshop.repository.ProductRepository;
import com.revshop.repository.UserRepository;
import com.revshop.service.FavoriteService;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class FavoriteServiceImpl implements FavoriteService {

    private final FavoriteRepository favoriteRepository;
    private final UserRepository userRepository;
    private final ProductRepository productRepository;

    public FavoriteServiceImpl(FavoriteRepository favoriteRepository,
            UserRepository userRepository,
            ProductRepository productRepository) {
        this.favoriteRepository = favoriteRepository;
        this.userRepository = userRepository;
        this.productRepository = productRepository;
    }

    @Override
    public Favorite addFavorite(Long buyerId, Long productId) {

        User buyer = findUserById(buyerId);
        Product product = findProductById(productId);

        if (favoriteRepository.existsByBuyerIdAndProductId(buyerId, productId)) {
            throw new DuplicateResourceException("Product is already in your favorites");
        }

        Favorite favorite = new Favorite();
        favorite.setBuyer(buyer);
        favorite.setProduct(product);
        favorite.setAddedAt(LocalDateTime.now());

        return favoriteRepository.save(favorite);
    }

    @Override
    public void removeFavorite(Long buyerId, Long productId) {

        Favorite favorite = favoriteRepository.findByBuyerIdAndProductId(buyerId, productId)
                .orElseThrow(() -> new ResourceNotFoundException("Favorite not found"));

        favoriteRepository.delete(favorite);
    }

    @Override
    public List<Favorite> getFavoritesByBuyer(Long buyerId) {
        return favoriteRepository.findByBuyerIdOrderByAddedAtDesc(buyerId);
    }

    @Override
    public boolean isFavorite(Long buyerId, Long productId) {
        return favoriteRepository.existsByBuyerIdAndProductId(buyerId, productId);
    }

    // ── DRY Helpers ───────────────────────────────────────────────────

    private User findUserById(Long buyerId) {
        return userRepository.findById(buyerId)
                .orElseThrow(() -> new ResourceNotFoundException("Buyer not found with id: " + buyerId));
    }

    private Product findProductById(Long productId) {
        return productRepository.findById(productId)
                .orElseThrow(() -> new ResourceNotFoundException("Product not found with id: " + productId));
    }
}
