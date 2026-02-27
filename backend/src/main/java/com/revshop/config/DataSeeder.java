package com.revshop.config;

import com.revshop.model.*;
import com.revshop.repository.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Component
public class DataSeeder implements CommandLineRunner {

    private static final Logger logger = LoggerFactory.getLogger(DataSeeder.class);

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private CategoryRepository categoryRepository;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private CartRepository cartRepository;

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Override
    @Transactional
    public void run(String... args) throws Exception {
        logger.info("Checking if database already has users...");
        if (userRepository.count() > 0) {
            logger.info("Database is already populated. Skipping DataSeeder.");
            return;
        }

        logger.info("Initializing Dummy Data...");

        // 1. Create Users (Buyers and Sellers)
        User seller = new User();
        seller.setName("Tech Store");
        seller.setEmail("tech@revshop.com");
        seller.setPassword(passwordEncoder.encode("password123"));
        seller.setRole(Role.SELLER);
        seller = userRepository.save(seller);

        User seller2 = new User();
        seller2.setName("Fashion Hub");
        seller2.setEmail("fashion@revshop.com");
        seller2.setPassword(passwordEncoder.encode("password123"));
        seller2.setRole(Role.SELLER);
        seller2 = userRepository.save(seller2);

        User seller3 = new User();
        seller3.setName("Home & Decor");
        seller3.setEmail("home@revshop.com");
        seller3.setPassword(passwordEncoder.encode("password123"));
        seller3.setRole(Role.SELLER);
        seller3 = userRepository.save(seller3);

        User buyer = new User();
        buyer.setName("Pavan Kalyan");
        buyer.setEmail("pavan@example.com");
        buyer.setPassword(passwordEncoder.encode("pavan123"));
        buyer.setRole(Role.BUYER);
        buyer = userRepository.save(buyer);

        User buyer2 = new User();
        buyer2.setName("John Doe");
        buyer2.setEmail("john@example.com");
        buyer2.setPassword(passwordEncoder.encode("john123"));
        buyer2.setRole(Role.BUYER);
        buyer2 = userRepository.save(buyer2);

        logger.info("Users created.");

        // 2. Create Categories
        Category electronics = new Category();
        electronics.setName("Electronics");
        electronics = categoryRepository.save(electronics);

        Category clothes = new Category();
        clothes.setName("Clothing");
        clothes = categoryRepository.save(clothes);

        Category homeAndKitchen = new Category();
        homeAndKitchen.setName("Home & Kitchen");
        homeAndKitchen = categoryRepository.save(homeAndKitchen);

        logger.info("Categories created.");

        // 3. Create Products
        Product p1 = new Product();
        p1.setName("Gaming Laptop");
        p1.setDescription("High performance gaming laptop with RTX 4070.");
        p1.setPrice(120000.0);
        p1.setMrp(150000.0);
        p1.setDiscountPercentage(20.0);
        p1.setQuantity(10);
        p1.setCategory(electronics);
        p1.setCategoryName("Electronics");
        p1.setSeller(seller);
        p1.setActive(true);
        p1.setStockThreshold(5);
        p1 = productRepository.save(p1);

        Product p2 = new Product();
        p2.setName("Wireless Headphones");
        p2.setDescription("Noise cancelling over-ear headphones.");
        p2.setPrice(15000.0);
        p2.setMrp(20000.0);
        p2.setDiscountPercentage(25.0);
        p2.setQuantity(50);
        p2.setCategory(electronics);
        p2.setCategoryName("Electronics");
        p2.setSeller(seller);
        p2.setActive(true);
        p2.setStockThreshold(10);
        p2 = productRepository.save(p2);

        Product p3 = new Product();
        p3.setName("Men's Graphic T-Shirt");
        p3.setDescription("100% Cotton, comfortable fit t-shirt.");
        p3.setPrice(800.0);
        p3.setMrp(1200.0);
        p3.setDiscountPercentage(33.0);
        p3.setQuantity(100);
        p3.setCategory(clothes);
        p3.setCategoryName("Clothing");
        p3.setSeller(seller2);
        p3.setActive(true);
        p3.setStockThreshold(20);
        p3 = productRepository.save(p3);

        Product p4 = new Product();
        p4.setName("Smart Watch Pro");
        p4.setDescription("Fitness tracker with heart rate monitor and GPS.");
        p4.setPrice(5000.0);
        p4.setMrp(7000.0);
        p4.setDiscountPercentage(28.0);
        p4.setQuantity(40);
        p4.setCategory(electronics);
        p4.setCategoryName("Electronics");
        p4.setSeller(seller);
        p4.setActive(true);
        p4.setStockThreshold(10);
        p4 = productRepository.save(p4);

        Product p5 = new Product();
        p5.setName("Ceramic Coffee Mug");
        p5.setDescription("Premium quality double-walled ceramic mug.");
        p5.setPrice(450.0);
        p5.setMrp(600.0);
        p5.setDiscountPercentage(25.0);
        p5.setQuantity(200);
        p5.setCategory(homeAndKitchen);
        p5.setCategoryName("Home & Kitchen");
        p5.setSeller(seller3);
        p5.setActive(true);
        p5.setStockThreshold(50);
        p5 = productRepository.save(p5);

        Product p6 = new Product();
        p6.setName("Classic Aviator Sunglasses");
        p6.setDescription("Polarized UV400 lens sunglasses.");
        p6.setPrice(1200.0);
        p6.setMrp(2000.0);
        p6.setDiscountPercentage(40.0);
        p6.setQuantity(60);
        p6.setCategory(clothes);
        p6.setCategoryName("Clothing");
        p6.setSeller(seller2);
        p6.setActive(true);
        p6.setStockThreshold(15);
        p6 = productRepository.save(p6);

        logger.info("Products created.");

        // 4. Create Cart for Buyer (Pavan)
        Cart cart = new Cart();
        cart.setUser(buyer);

        CartItem cItem1 = new CartItem();
        cItem1.setProduct(p2); // Headphones
        cItem1.setQuantity(2);
        cart.addItem(cItem1);

        CartItem cItem2 = new CartItem();
        cItem2.setProduct(p3); // T-Shirt
        cItem2.setQuantity(3);
        cart.addItem(cItem2);

        cartRepository.save(cart);

        logger.info("Cart initialized for buyer 'pavan@example.com'.");

        // 5. Create an Past Order for Buyer (John)
        Order order = new Order();
        order.setBuyer(buyer2);
        order.setStatus(OrderStatus.DELIVERED);
        order.setShippingAddress("123 Elm Street, Cityville");
        order.setBillingAddress("123 Elm Street, Cityville");
        order.setPaymentMethod("Credit Card");
        order.setPaymentStatus("PAID");
        order.setOrderDate(LocalDateTime.now().minusDays(5));

        OrderItem oItem1 = new OrderItem();
        oItem1.setProduct(p1);
        oItem1.setQuantity(1);
        oItem1.setPriceAtPurchase(120000.0);
        order.addOrderItem(oItem1);

        order.setTotalAmount(oItem1.getSubtotal());
        orderRepository.save(order);

        logger.info("Dummy order history created.");

        logger.info("----- Data Seeding Completed Successfully! -----");
    }
}
