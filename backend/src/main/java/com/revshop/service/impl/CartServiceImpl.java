package com.revshop.service.impl;

import com.revshop.dto.*;
import com.revshop.exception.InsufficientStockException;
import com.revshop.exception.ResourceNotFoundException;
import com.revshop.model.*;
import com.revshop.repository.*;
import com.revshop.service.CartService;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class CartServiceImpl implements CartService {

    private final CartRepository cartRepository;
    private final CartItemRepository cartItemRepository;
    private final ProductRepository productRepository;
    private final UserRepository userRepository;

    public CartServiceImpl(CartRepository cartRepository,
            CartItemRepository cartItemRepository,
            ProductRepository productRepository,
            UserRepository userRepository) {
        this.cartRepository = cartRepository;
        this.cartItemRepository = cartItemRepository;
        this.productRepository = productRepository;
        this.userRepository = userRepository;
    }

    // ══════════════════════════════════════════════════════════════════════
    // Add Product to Cart
    // ══════════════════════════════════════════════════════════════════════

    @Override
    @Transactional
    public CartResponse addToCart(Long userId, AddToCartRequest request) {

        User user = findUserById(userId);
        Product product = findProductById(request.getProductId());

        validateStock(product, request.getQuantity());

        Cart cart = getOrCreateCart(userId, user);
        Optional<CartItem> existingItem = cart.getItems().stream()
                .filter(item -> item.getProduct().getId().equals(request.getProductId()))
                .findFirst();

        if (existingItem.isPresent()) {
            CartItem item = existingItem.get();
            int newQuantity = item.getQuantity() + request.getQuantity();
            validateStock(product, newQuantity);
            item.setQuantity(newQuantity);
        } else {
            CartItem newItem = new CartItem();
            newItem.setProduct(product);
            newItem.setQuantity(request.getQuantity());
            cart.addItem(newItem);
        }

        Cart savedCart = cartRepository.save(cart);
        return mapToResponse(savedCart);
    }

    // ══════════════════════════════════════════════════════════════════════
    // Update Cart Quantity
    // ══════════════════════════════════════════════════════════════════════

    @Override
    @Transactional
    public CartResponse updateCartItemQuantity(Long userId, Long cartItemId, UpdateCartRequest request) {

        Cart cart = getCartForUser(userId);
        CartItem cartItem = findCartItemById(cart, cartItemId);

        validateStock(cartItem.getProduct(), request.getQuantity());
        cartItem.setQuantity(request.getQuantity());

        Cart savedCart = cartRepository.save(cart);
        return mapToResponse(savedCart);
    }

    // ══════════════════════════════════════════════════════════════════════
    // Remove Product & View Cart
    // ══════════════════════════════════════════════════════════════════════

    @Override
    @Transactional
    public CartResponse removeFromCart(Long userId, Long cartItemId) {

        Cart cart = getCartForUser(userId);
        CartItem cartItem = findCartItemById(cart, cartItemId);

        cart.removeItem(cartItem);
        cartItemRepository.delete(cartItem);

        Cart savedCart = cartRepository.save(cart);
        return mapToResponse(savedCart);
    }

    @Override
    public CartResponse getCart(Long userId) {
        return cartRepository.findByUserId(userId)
                .map(this::mapToResponse)
                .orElseGet(this::emptyCartResponse);
    }

    @Override
    @Transactional
    public void clearCart(Long userId) {
        Cart cart = getCartForUser(userId);
        cart.getItems().clear();
        cartRepository.save(cart);
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

    private CartItem findCartItemById(Cart cart, Long cartItemId) {
        return cart.getItems().stream()
                .filter(item -> item.getId().equals(cartItemId))
                .findFirst()
                .orElseThrow(() -> new ResourceNotFoundException("Cart item not found with id: " + cartItemId));
    }

    private void validateStock(Product product, int requestedQuantity) {
        if (requestedQuantity > product.getQuantity()) {
            throw new InsufficientStockException(
                    "Requested quantity (" + requestedQuantity +
                            ") exceeds available stock (" + product.getQuantity() + ")");
        }
    }

    private Cart getOrCreateCart(Long userId, User user) {
        return cartRepository.findByUserId(userId).orElseGet(() -> {
            Cart newCart = new Cart();
            newCart.setUser(user);
            return cartRepository.save(newCart);
        });
    }

    private Cart getCartForUser(Long userId) {
        return cartRepository.findByUserId(userId)
                .orElseThrow(() -> new ResourceNotFoundException("Cart not found for user: " + userId));
    }

    private CartResponse emptyCartResponse() {
        return new CartResponse(null, List.of(), 0.0, 0);
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
