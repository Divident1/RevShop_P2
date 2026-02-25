package com.revshop.service;

import com.revshop.dto.ReviewRequest;
import com.revshop.model.Review;

import java.util.List;

public interface ReviewService {

    Review addReview(ReviewRequest request);

    List<Review> getReviewsByProduct(Long productId);

    List<Review> getReviewsByBuyer(Long buyerId);

    Double getAverageRating(Long productId);

    void deleteReview(Long reviewId);

}
