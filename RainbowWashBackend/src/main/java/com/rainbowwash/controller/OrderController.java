package com.rainbowwash.controller;

import com.rainbowwash.dto.OrderRequest;
import com.rainbowwash.dto.OrderUpdateRequest;
import com.rainbowwash.model.Order;
import com.rainbowwash.service.OrderService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/orders")
public class OrderController {

    private final OrderService orderService;

    public OrderController(OrderService orderService) {
        this.orderService = orderService;
    }

    // Public — customers place laundry orders without an account.
    @PostMapping
    public ResponseEntity<Order> createOrder(@Valid @RequestBody OrderRequest request) {
        return ResponseEntity.ok(orderService.createOrder(request));
    }

    // Public — the Track Order page looks up a single order by its reference,
    // with no login required. Deliberately narrower than the full GET / list
    // below, which requires staff auth and would otherwise expose every
    // customer's name, phone and total to anyone who asks.
    @GetMapping("/track/{referenceId}")
    public ResponseEntity<Order> trackOrder(@PathVariable String referenceId) {
        Order order = orderService.findByReference(referenceId);
        return order != null ? ResponseEntity.ok(order) : ResponseEntity.notFound().build();
    }

    // Authenticated (dashboard) — full list, Today/History filtering happens client-side.
    @GetMapping
    public ResponseEntity<List<Order>> getAllOrders() {
        return ResponseEntity.ok(orderService.getAllOrders());
    }

    // Authenticated (dashboard) — status, total, payment status, archived, printed.
    @PatchMapping("/{id}")
    public ResponseEntity<Order> updateOrder(@PathVariable Long id, @RequestBody OrderUpdateRequest request) {
        return ResponseEntity.ok(orderService.updateOrder(id, request));
    }
}
