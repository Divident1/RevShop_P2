package com.revshop.service.impl;

import com.revshop.dto.ReviewRequest;
import com.revshop.exception.DuplicateResourceException;
import com.revshop.exception.ForbiddenException;
import com.revshop.exception.ResourceNotFoundException;
import com.revshop.model.Order;
import com.revshop.model.OrderStatus;
import com.revshop.model.Product;
import com.revshop.model.Review;
import com.revshop.model.User;
import com.revshop.repository.OrderRepository;
import com.revshop.repository.ProductRepository;
import com.revshop.repository.ReviewRepository;
import com.revshop.repository.UserRepository;
import com.revshop.service.NotificationService;
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
    private final NotificationService notificationService;

    public ReviewServiceImpl(ReviewRepository reviewRepository,
            UserRepository userRepository,
            ProductRepository productRepository,
            OrderRepository orderRepository,
            NotificationService notificationService) {
        this.reviewRepository = reviewRepository;
        this.userRepository = userRepository;
        this.productRepository = productRepository;
        this.orderRepository = orderRepository;
        this.notificationService = notificationService;
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

        Review savedReview = reviewRepository.save(review);
        syncProductRating(product.getId());
        notifySellerAboutReview(savedReview);

        return savedReview;
    }

    @Override
    public List<Review> getReviewsByProduct(Long productId) {
        return reviewRepository.findByProductIdOrderByCreatedAtDesc(productId);
    }

    @Override
    public List<Review> getReviewsBySeller(Long sellerId) {
        return reviewRepository.findByProductSeller_IdOrderByCreatedAtDesc(sellerId);
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
        Review review = reviewRepository.findById(reviewId)
                .orElseThrow(() -> new ResourceNotFoundException("Review not found with id: " + reviewId));

        Long productId = review.getProduct().getId();
        reviewRepository.deleteById(reviewId);
        syncProductRating(productId);
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
                .filter(order -> order.getStatus() != OrderStatus.CANCELLED)
                .flatMap(order -> order.getOrderItems().stream())
                .anyMatch(item -> item.getProduct().getId().equals(productId));

        if (!hasPurchased) {
            throw new ForbiddenException("You can only review products you have purchased");
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

    private void syncProductRating(Long productId) {
        Product product = findProductById(productId);
        product.setRating(getAverageRating(productId));
        productRepository.save(product);
    }

    private void notifySellerAboutReview(Review review) {
        User seller = review.getProduct().getSeller();
        if (seller == null || seller.getId() == null) {
            return;
        }

        String commentPreview = review.getComment() == null ? "" : review.getComment().trim();
        if (!commentPreview.isEmpty() && commentPreview.length() > 80) {
            commentPreview = commentPreview.substring(0, 80) + "...";
        }

        String message = "⭐ New " + review.getRating() + "-star review on '" + review.getProduct().getName() + "'.";
        if (!commentPreview.isEmpty()) {
            message = message + " Comment: \"" + commentPreview + "\"";
        }

        notificationService.createNotification(seller.getId(), message);
    }
}
