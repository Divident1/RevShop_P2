package com.revshop.controller;

import com.revshop.dto.AddToCartRequest;
import com.revshop.dto.CartResponse;
import com.revshop.dto.UpdateCartRequest;
import com.revshop.service.CartService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@CrossOrigin(origins = "http://localhost:4200")
@RestController
@RequestMapping("/api/cart")
public class CartController {

    @Autowired
    private CartService cartService;

    // ── 7️⃣ Add Product to Cart ──────────────────────────────────────────

    @PostMapping("/{userId}/items")
    public ResponseEntity<CartResponse> addToCart(
            @PathVariable Long userId,
            @Valid @RequestBody AddToCartRequest request) {

        CartResponse response = cartService.addToCart(userId, request);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    // ── 8️⃣ Update Cart Quantity ─────────────────────────────────────────

    @PutMapping("/{userId}/items/{cartItemId}")
    public ResponseEntity<CartResponse> updateCartItemQuantity(
            @PathVariable Long userId,
            @PathVariable Long cartItemId,
            @Valid @RequestBody UpdateCartRequest request) {

        CartResponse response = cartService.updateCartItemQuantity(userId, cartItemId, request);
        return ResponseEntity.ok(response);
    }

    // ── 9️⃣ Remove Product from Cart ─────────────────────────────────────

    @DeleteMapping("/{userId}/items/{cartItemId}")
    public ResponseEntity<CartResponse> removeFromCart(
            @PathVariable Long userId,
            @PathVariable Long cartItemId) {

        CartResponse response = cartService.removeFromCart(userId, cartItemId);
        return ResponseEntity.ok(response);
    }

    // ── 9️⃣ View Cart ────────────────────────────────────────────────────

    @GetMapping("/{userId}")
    public ResponseEntity<CartResponse> getCart(@PathVariable Long userId) {

        CartResponse response = cartService.getCart(userId);
        return ResponseEntity.ok(response);
    }

    // ── Clear Cart ────────────────────────────────────────────────────────

    @DeleteMapping("/{userId}")
    public ResponseEntity<Void> clearCart(@PathVariable Long userId) {

        cartService.clearCart(userId);
        return ResponseEntity.noContent().build();
    }
}
