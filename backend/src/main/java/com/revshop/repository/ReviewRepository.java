package com.revshop.repository;

import com.revshop.model.Review;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface ReviewRepository extends JpaRepository<Review, Long> {

    List<Review> findByProductIdOrderByCreatedAtDesc(Long productId);

    List<Review> findByProductSeller_IdOrderByCreatedAtDesc(Long sellerId);

    List<Review> findByBuyerIdOrderByCreatedAtDesc(Long buyerId);

    Optional<Review> findByBuyerIdAndProductId(Long buyerId, Long productId);

    @Query("SELECT AVG(r.rating) FROM Review r WHERE r.product.id = :productId")
    Double findAverageRatingByProductId(Long productId);

    boolean existsByBuyerIdAndProductId(Long buyerId, Long productId);

}
