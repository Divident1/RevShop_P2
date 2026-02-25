package com.revshop.service;

import com.revshop.dto.AddToCartRequest;
import com.revshop.dto.CartResponse;
import com.revshop.dto.UpdateCartRequest;

public interface CartService {

    // 7️⃣ Add Product to Cart
    CartResponse addToCart(Long userId, AddToCartRequest request);

    // 8️⃣ Update Cart Quantity
    CartResponse updateCartItemQuantity(Long userId, Long cartItemId, UpdateCartRequest request);

    // 9️⃣ Remove Product from Cart
    CartResponse removeFromCart(Long userId, Long cartItemId);

    // 9️⃣ View Cart
    CartResponse getCart(Long userId);

    // Clear entire cart
    void clearCart(Long userId);
}
