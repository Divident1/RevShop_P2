package com.revshop.service.impl;

import com.revshop.dto.ReviewRequest;
import com.revshop.exception.DuplicateResourceException;
import com.revshop.exception.ResourceNotFoundException;
import com.revshop.model.*;
import com.revshop.repository.OrderRepository;
import com.revshop.repository.ProductRepository;
import com.revshop.repository.ReviewRepository;
import com.revshop.repository.UserRepository;
import com.revshop.service.NotificationService;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.contains;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ReviewServiceImplTest {

    @Mock
    private ReviewRepository reviewRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private ProductRepository productRepository;

    @Mock
    private OrderRepository orderRepository;

    @Mock
    private NotificationService notificationService;

    @InjectMocks
    private ReviewServiceImpl reviewService;

    private User buyer;
    private Product product;
    private Review review;

    @BeforeEach
    void setUp() {
        User seller = new User();
        seller.setId(2L);
        seller.setName("Seller");
        seller.setRole(Role.SELLER);

        buyer = new User();
        buyer.setId(1L);
        buyer.setName("Buyer");
        buyer.setEmail("buyer@revshop.com");
        buyer.setRole(Role.BUYER);

        product = new Product();
        product.setId(1L);
        product.setName("Test Phone");
        product.setPrice(15000.0);
        product.setSeller(seller);

        review = new Review();
        review.setId(1L);
        review.setBuyer(buyer);
        review.setProduct(product);
        review.setRating(5);
        review.setComment("Excellent product!");
        review.setCreatedAt(LocalDateTime.now());
    }

    @Test
    @DisplayName("AddReview - should add review successfully")
    void addReview_Success() {
        ReviewRequest request = new ReviewRequest();
        request.setBuyerId(1L);
        request.setProductId(1L);
        request.setRating(5);
        request.setComment("Great product!");

        OrderItem orderItem = new OrderItem();
        orderItem.setProduct(product);
        Order order = new Order();
        order.setId(1L);
        order.setOrderItems(new ArrayList<>(List.of(orderItem)));

        when(userRepository.findById(1L)).thenReturn(Optional.of(buyer));
        when(productRepository.findById(1L)).thenReturn(Optional.of(product));
        when(orderRepository.findByBuyerIdOrderByOrderDateDesc(1L)).thenReturn(List.of(order));
        when(reviewRepository.existsByBuyerIdAndProductId(1L, 1L)).thenReturn(false);
        when(reviewRepository.save(any(Review.class))).thenReturn(review);
        when(reviewRepository.findAverageRatingByProductId(1L)).thenReturn(4.8);

        Review result = reviewService.addReview(request);

        assertNotNull(result);
        assertEquals(5, result.getRating());
        verify(reviewRepository, times(1)).save(any(Review.class));
        verify(notificationService, times(1)).createNotification(eq(2L), contains("New 5-star review"));
    }

    @Test
    @DisplayName("AddReview - should throw when buyer hasn't purchased product")
    void addReview_NotPurchased() {
        ReviewRequest request = new ReviewRequest();
        request.setBuyerId(1L);
        request.setProductId(1L);
        request.setRating(5);
        request.setComment("Good");

        when(userRepository.findById(1L)).thenReturn(Optional.of(buyer));
        when(productRepository.findById(1L)).thenReturn(Optional.of(product));
        when(orderRepository.findByBuyerIdOrderByOrderDateDesc(1L)).thenReturn(List.of());

        Exception ex = assertThrows(Exception.class,
                () -> reviewService.addReview(request));

        assertTrue(ex.getMessage().contains("purchased"));
    }

    @Test
    @DisplayName("AddReview - should throw when already reviewed")
    void addReview_AlreadyReviewed() {
        ReviewRequest request = new ReviewRequest();
        request.setBuyerId(1L);
        request.setProductId(1L);
        request.setRating(4);
        request.setComment("Nice");

        OrderItem orderItem = new OrderItem();
        orderItem.setProduct(product);
        Order order = new Order();
        order.setId(1L);
        order.setOrderItems(new ArrayList<>(List.of(orderItem)));

        when(userRepository.findById(1L)).thenReturn(Optional.of(buyer));
        when(productRepository.findById(1L)).thenReturn(Optional.of(product));
        when(orderRepository.findByBuyerIdOrderByOrderDateDesc(1L)).thenReturn(List.of(order));
        when(reviewRepository.existsByBuyerIdAndProductId(1L, 1L)).thenReturn(true);

        Exception ex = assertThrows(DuplicateResourceException.class,
                () -> reviewService.addReview(request));

        assertTrue(ex.getMessage().contains("already reviewed"));
    }

    @Test
    @DisplayName("AddReview - should throw when rating is out of range")
    void addReview_InvalidRating() {
        ReviewRequest request = new ReviewRequest();
        request.setBuyerId(1L);
        request.setProductId(1L);
        request.setRating(6); // Invalid: > 5
        request.setComment("Invalid");

        OrderItem orderItem = new OrderItem();
        orderItem.setProduct(product);
        Order order = new Order();
        order.setId(1L);
        order.setOrderItems(new ArrayList<>(List.of(orderItem)));

        when(userRepository.findById(1L)).thenReturn(Optional.of(buyer));
        when(productRepository.findById(1L)).thenReturn(Optional.of(product));
        when(orderRepository.findByBuyerIdOrderByOrderDateDesc(1L)).thenReturn(List.of(order));
        when(reviewRepository.existsByBuyerIdAndProductId(1L, 1L)).thenReturn(false);

        assertThrows(IllegalArgumentException.class, () -> reviewService.addReview(request));
    }

    @Test
    @DisplayName("AddReview - should throw when order was cancelled")
    void addReview_CancelledOrderDoesNotQualify() {
        ReviewRequest request = new ReviewRequest();
        request.setBuyerId(1L);
        request.setProductId(1L);
        request.setRating(5);
        request.setComment("Good");

        OrderItem orderItem = new OrderItem();
        orderItem.setProduct(product);
        Order cancelledOrder = new Order();
        cancelledOrder.setId(2L);
        cancelledOrder.setStatus(OrderStatus.CANCELLED);
        cancelledOrder.setOrderItems(new ArrayList<>(List.of(orderItem)));

        when(userRepository.findById(1L)).thenReturn(Optional.of(buyer));
        when(productRepository.findById(1L)).thenReturn(Optional.of(product));
        when(orderRepository.findByBuyerIdOrderByOrderDateDesc(1L)).thenReturn(List.of(cancelledOrder));

        Exception ex = assertThrows(Exception.class, () -> reviewService.addReview(request));
        assertTrue(ex.getMessage().contains("purchased"));
    }

    @Test
    @DisplayName("GetReviewsByProduct - should return reviews for a product")
    void getReviewsByProduct_Success() {
        when(reviewRepository.findByProductIdOrderByCreatedAtDesc(1L))
                .thenReturn(List.of(review));

        List<Review> results = reviewService.getReviewsByProduct(1L);

        assertEquals(1, results.size());
        assertEquals("Excellent product!", results.get(0).getComment());
    }

    @Test
    @DisplayName("GetReviewsByBuyer - should return reviews by a buyer")
    void getReviewsByBuyer_Success() {
        when(reviewRepository.findByBuyerIdOrderByCreatedAtDesc(1L))
                .thenReturn(List.of(review));

        List<Review> results = reviewService.getReviewsByBuyer(1L);

        assertEquals(1, results.size());
    }

    @Test
    @DisplayName("GetAverageRating - should return average rating")
    void getAverageRating_Success() {
        when(reviewRepository.findAverageRatingByProductId(1L)).thenReturn(4.5);

        Double avg = reviewService.getAverageRating(1L);

        assertEquals(4.5, avg);
    }

    @Test
    @DisplayName("GetAverageRating - should return 0 when no reviews")
    void getAverageRating_NoReviews() {
        when(reviewRepository.findAverageRatingByProductId(1L)).thenReturn(null);

        Double avg = reviewService.getAverageRating(1L);

        assertEquals(0.0, avg);
    }

    @Test
    @DisplayName("DeleteReview - should delete review successfully")
    void deleteReview_Success() {
        when(reviewRepository.findById(1L)).thenReturn(Optional.of(review));
        when(reviewRepository.findAverageRatingByProductId(1L)).thenReturn(4.5);
        when(productRepository.findById(1L)).thenReturn(Optional.of(product));
        doNothing().when(reviewRepository).deleteById(1L);

        assertDoesNotThrow(() -> reviewService.deleteReview(1L));
        verify(reviewRepository, times(1)).deleteById(1L);
        verify(productRepository, times(1)).save(any(Product.class));
    }

    @Test
    @DisplayName("DeleteReview - should throw when review not found")
    void deleteReview_NotFound() {
        when(reviewRepository.findById(999L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> reviewService.deleteReview(999L));
    }

    @Test
    @DisplayName("GetReviewsBySeller - should return seller product reviews")
    void getReviewsBySeller_Success() {
        when(reviewRepository.findByProductSeller_IdOrderByCreatedAtDesc(2L))
                .thenReturn(List.of(review));

        List<Review> results = reviewService.getReviewsBySeller(2L);

        assertEquals(1, results.size());
        assertEquals(1L, results.get(0).getProduct().getId());
    }
}
