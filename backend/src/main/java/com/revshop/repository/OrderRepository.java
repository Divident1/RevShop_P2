package com.revshop.repository;

import com.revshop.model.Order;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface OrderRepository extends JpaRepository<Order, Long> {

    List<Order> findByBuyerIdOrderByOrderDateDesc(Long buyerId);

    List<Order> findByOrderItems_Product_Seller_IdOrderByOrderDateDesc(Long sellerId);

}
