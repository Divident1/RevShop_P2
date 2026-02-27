package com.revshop.service.impl;

import com.revshop.dto.ReviewRequest;
import com.revshop.exception.DuplicateResourceException;
import com.revshop.exception.ResourceNotFoundException;
import com.revshop.model.Order;
import com.revshop.model.Product;
import com.revshop.model.Review;
import com.revshop.model.User;
import com.revshop.repository.OrderRepository;
import com.revshop.repository.ProductRepository;
import com.revshop.repository.ReviewRepository;
import com.revshop.repository.UserRepository;
import com.revshop.service.ReviewService;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class ReviewServiceImpl implements ReviewService {

    private final ReviewRepository reviewRepository;
    private final UserRepository userRepository;
    private final ProductRepository productRepository;
    private final OrderRepository orderRepository;

    public ReviewServiceImpl(ReviewRepository reviewRepository,
            UserRepository userRepository,
            ProductRepository productRepository,
            OrderRepository orderRepository) {
        this.reviewRepository = reviewRepository;
        this.userRepository = userRepository;
        this.productRepository = productRepository;
        this.orderRepository = orderRepository;
    }

    @Override
    public Review addReview(ReviewRequest request) {

        User buyer = findUserById(request.getBuyerId());
        Product product = findProductById(request.getProductId());

        validatePurchaseHistory(request.getBuyerId(), request.getProductId());
        validateNoDuplicateReview(request.getBuyerId(), request.getProductId());
        validateRating(request.getRating());

        Review review = new Review();
        review.setBuyer(buyer);
        review.setProduct(product);
        review.setRating(request.getRating());
        review.setComment(request.getComment());
        review.setCreatedAt(LocalDateTime.now());

        return reviewRepository.save(review);
    }

    @Override
    public List<Review> getReviewsByProduct(Long productId) {
        return reviewRepository.findByProductIdOrderByCreatedAtDesc(productId);
    }

    @Override
    public List<Review> getReviewsByBuyer(Long buyerId) {
        return reviewRepository.findByBuyerIdOrderByCreatedAtDesc(buyerId);
    }

    @Override
    public Double getAverageRating(Long productId) {
        Double averageRating = reviewRepository.findAverageRatingByProductId(productId);
        return averageRating != null ? averageRating : 0.0;
    }

    @Override
    public void deleteReview(Long reviewId) {
        if (!reviewRepository.existsById(reviewId)) {
            throw new ResourceNotFoundException("Review not found with id: " + reviewId);
        }
        reviewRepository.deleteById(reviewId);
    }

    // ── DRY Helpers ───────────────────────────────────────────────────

    private User findUserById(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("Buyer not found with id: " + userId));
    }

    private Product findProductById(Long productId) {
        return productRepository.findById(productId)
                .orElseThrow(() -> new ResourceNotFoundException("Product not found with id: " + productId));
    }

    private void validatePurchaseHistory(Long buyerId, Long productId) {
        List<Order> buyerOrders = orderRepository.findByBuyerIdOrderByOrderDateDesc(buyerId);
        boolean hasPurchased = buyerOrders.stream()
                .flatMap(order -> order.getOrderItems().stream())
                .anyMatch(item -> item.getProduct().getId().equals(productId));

        if (!hasPurchased) {
            throw new ForbiddenReviewException("You can only review products you have purchased");
        }
    }

    private void validateNoDuplicateReview(Long buyerId, Long productId) {
        if (reviewRepository.existsByBuyerIdAndProductId(buyerId, productId)) {
            throw new DuplicateResourceException("You have already reviewed this product");
        }
    }

    private void validateRating(int rating) {
        if (rating < 1 || rating > 5) {
            throw new IllegalArgumentException("Rating must be between 1 and 5, got: " + rating);
        }
    }

    /**
     * Private inner exception for review-specific business rule violations.
     * Caught by the RuntimeException handler in GlobalExceptionHandler.
     */
    static class ForbiddenReviewException extends RuntimeException {
        ForbiddenReviewException(String message) {
            super(message);
        }
    }
}
