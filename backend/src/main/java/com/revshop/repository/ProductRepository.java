package com.revshop.repository;

import com.revshop.model.Product;
import org.springframework.data.domain.*;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {

    // Jatin's buyer queries
    Page<Product> findByCategory_IdAndActiveTrue(Long categoryId, Pageable pageable);

    @Query("SELECT p FROM Product p WHERE " +
            "LOWER(p.name) LIKE LOWER(CONCAT('%', :keyword, '%')) " +
            "AND p.active = true")
    List<Product> searchProducts(@Param("keyword") String keyword);

    List<Product> findBySeller_Id(Long sellerId);
}
