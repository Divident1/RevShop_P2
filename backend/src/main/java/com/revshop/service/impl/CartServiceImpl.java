package com.revshop.service.impl;

import com.revshop.dto.*;
import com.revshop.exception.ResourceNotFoundException;
import com.revshop.model.*;
import com.revshop.repository.*;
import com.revshop.service.CartService;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class CartServiceImpl implements CartService {

    private static final Logger logger = LogManager.getLogger(CartServiceImpl.class);

    @Autowired
    private CartRepository cartRepository;

    @Autowired
    private CartItemRepository cartItemRepository;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private UserRepository userRepository;

    // ══════════════════════════════════════════════════════════════════════
    // 7️⃣ Add Product to Cart
    // ══════════════════════════════════════════════════════════════════════

    @Override
    @Transactional
    public CartResponse addToCart(Long userId, AddToCartRequest request) {

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + userId));

        Product product = productRepository.findById(request.getProductId())
                .orElseThrow(
                        () -> new ResourceNotFoundException("Product not found with id: " + request.getProductId()));

        // Stock validation
        if (product.getQuantity() < request.getQuantity()) {
            throw new IllegalArgumentException(
                    "Requested quantity (" + request.getQuantity() +
                            ") exceeds available stock (" + product.getQuantity() + ")");
        }

        // Get or create cart
        Cart cart = cartRepository.findByUserId(userId)
                .orElseGet(() -> {
                    Cart newCart = new Cart();
                    newCart.setUser(user);
                    return cartRepository.save(newCart);
                });

        // Check if product already exists in cart
        Optional<CartItem> existingItem = cart.getItems().stream()
                .filter(item -> item.getProduct().getId().equals(request.getProductId()))
                .findFirst();

        if (existingItem.isPresent()) {
            // Update quantity of existing item
            CartItem item = existingItem.get();
            int newQuantity = item.getQuantity() + request.getQuantity();

            if (newQuantity > product.getQuantity()) {
                throw new IllegalArgumentException(
                        "Total quantity (" + newQuantity +
                                ") exceeds available stock (" + product.getQuantity() + ")");
            }

            item.setQuantity(newQuantity);
            logger.info("Updated cart item quantity for product {} to {}", product.getName(), newQuantity);
        } else {
            // Add new item to cart
            CartItem newItem = new CartItem();
            newItem.setProduct(product);
            newItem.setQuantity(request.getQuantity());
            cart.addItem(newItem);
            logger.info("Added product {} to cart for user {}", product.getName(), userId);
        }

        Cart savedCart = cartRepository.save(cart);
        return mapToResponse(savedCart);
    }

    // ══════════════════════════════════════════════════════════════════════
    // 8️⃣ Update Cart Quantity
    // ══════════════════════════════════════════════════════════════════════

    @Override
    @Transactional
    public CartResponse updateCartItemQuantity(Long userId, Long cartItemId, UpdateCartRequest request) {

        Cart cart = getCartForUser(userId);

        CartItem cartItem = cart.getItems().stream()
                .filter(item -> item.getId().equals(cartItemId))
                .findFirst()
                .orElseThrow(() -> new ResourceNotFoundException("Cart item not found with id: " + cartItemId));

        // Stock validation — cannot exceed available stock
        Product product = cartItem.getProduct();
        if (request.getQuantity() > product.getQuantity()) {
            throw new IllegalArgumentException(
                    "Requested quantity (" + request.getQuantity() +
                            ") exceeds available stock (" + product.getQuantity() + ")");
        }

        cartItem.setQuantity(request.getQuantity());
        logger.info("Updated cart item {} quantity to {} for user {}", cartItemId, request.getQuantity(), userId);

        Cart savedCart = cartRepository.save(cart);
        return mapToResponse(savedCart);
    }

    // ══════════════════════════════════════════════════════════════════════
    // 9️⃣ Remove Product & View Cart
    // ══════════════════════════════════════════════════════════════════════

    @Override
    @Transactional
    public CartResponse removeFromCart(Long userId, Long cartItemId) {

        Cart cart = getCartForUser(userId);

        CartItem cartItem = cart.getItems().stream()
                .filter(item -> item.getId().equals(cartItemId))
                .findFirst()
                .orElseThrow(() -> new ResourceNotFoundException("Cart item not found with id: " + cartItemId));

        cart.removeItem(cartItem);
        cartItemRepository.delete(cartItem);
        logger.info("Removed cart item {} from cart for user {}", cartItemId, userId);

        Cart savedCart = cartRepository.save(cart);
        return mapToResponse(savedCart);
    }

    @Override
    public CartResponse getCart(Long userId) {

        Optional<Cart> optionalCart = cartRepository.findByUserId(userId);

        if (optionalCart.isEmpty()) {
            // Return empty cart response
            return new CartResponse(null, List.of(), 0.0, 0);
        }

        return mapToResponse(optionalCart.get());
    }

    @Override
    @Transactional
    public void clearCart(Long userId) {

        Cart cart = getCartForUser(userId);
        cart.getItems().clear();
        cartRepository.save(cart);
        logger.info("Cleared cart for user {}", userId);
    }

    // ══════════════════════════════════════════════════════════════════════
    // Helpers
    // ══════════════════════════════════════════════════════════════════════

    private Cart getCartForUser(Long userId) {
        return cartRepository.findByUserId(userId)
                .orElseThrow(() -> new ResourceNotFoundException("Cart not found for user: " + userId));
    }

    private CartResponse mapToResponse(Cart cart) {
        List<CartItemResponse> items = cart.getItems().stream()
                .map(this::mapItemToResponse)
                .collect(Collectors.toList());

        return new CartResponse(
                cart.getId(),
                items,
                cart.getTotalPrice(),
                cart.getTotalItems());
    }

    private CartItemResponse mapItemToResponse(CartItem item) {
        Product product = item.getProduct();
        return new CartItemResponse(
                item.getId(),
                product.getId(),
                product.getName(),
                product.getDescription(),
                product.getPrice(),
                product.getMrp(),
                product.getDiscountPercentage(),
                item.getQuantity(),
                product.getQuantity(), // available stock
                item.getSubtotal());
    }
}
