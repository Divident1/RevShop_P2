package com.revshop.controller;

import com.revshop.dto.*;
import com.revshop.model.OrderStatus;
import com.revshop.service.OrderService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@CrossOrigin(origins = "http://localhost:4200")
@RestController
@RequestMapping("/api/orders")
public class OrderController {

    private final OrderService orderService;

    public OrderController(OrderService orderService) {
        this.orderService = orderService;
    }

    @PostMapping
    public ResponseEntity<OrderResponse> placeOrder(@RequestBody OrderRequest request) {
        OrderResponse order = orderService.placeOrder(request);
        return new ResponseEntity<>(order, HttpStatus.CREATED);
    }

    @GetMapping("/buyer/{buyerId}")
    public ResponseEntity<List<OrderResponse>> getOrdersByBuyer(@PathVariable Long buyerId) {
        return ResponseEntity.ok(orderService.getOrdersByBuyer(buyerId));
    }

    @GetMapping("/seller/{sellerId}")
    public ResponseEntity<List<OrderResponse>> getOrdersBySeller(@PathVariable Long sellerId) {
        return ResponseEntity.ok(orderService.getOrdersBySeller(sellerId));
    }

    @GetMapping("/{orderId}")
    public ResponseEntity<OrderResponse> getOrderById(@PathVariable Long orderId) {
        return ResponseEntity.ok(orderService.getOrderById(orderId));
    }

    @PutMapping("/{orderId}/status")
    public ResponseEntity<OrderResponse> updateOrderStatus(
            @PathVariable Long orderId,
            @RequestParam OrderStatus status) {
        return ResponseEntity.ok(orderService.updateOrderStatus(orderId, status));
    }

    @PutMapping("/{orderId}/cancel")
    public ResponseEntity<String> cancelOrder(@PathVariable Long orderId) {
        orderService.cancelOrder(orderId);
        return ResponseEntity.ok("Order cancelled successfully");
    }

    @PostMapping("/checkout")
    public ResponseEntity<OrderResponseDto> checkout(@Valid @RequestBody CheckoutRequestDto request) {
        return ResponseEntity.ok(orderService.checkout(request));
    }

    @PostMapping("/payment")
    public ResponseEntity<OrderResponseDto> payment(@RequestBody PaymentRequestDto request) {
        return ResponseEntity.ok(orderService.processPayment(request));
    }
}
