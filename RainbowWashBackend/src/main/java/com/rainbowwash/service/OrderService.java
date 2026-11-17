package com.rainbowwash.service;

import com.rainbowwash.dto.OrderItemRequest;
import com.rainbowwash.dto.OrderRequest;
import com.rainbowwash.dto.OrderUpdateRequest;
import com.rainbowwash.model.Order;
import com.rainbowwash.model.OrderItem;
import com.rainbowwash.repository.OrderRepository;
import org.springframework.stereotype.Service;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import java.math.RoundingMode;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class OrderService {

    private final OrderRepository orderRepository;

    public OrderService(OrderRepository orderRepository) {
        this.orderRepository = orderRepository;
    }

    public Order createOrder(OrderRequest request) {
        Order order = new Order();
        order.setReferenceId("LND-" + UUID.randomUUID().toString().substring(0, 6).toUpperCase());
        order.setFulfilment(request.getFulfilment());
        order.setAddress(request.getAddress());
        order.setPreferredDate(request.getPreferredDate());
        order.setPreferredTime(request.getPreferredTime());
        order.setPaymentMethod(request.getPaymentMethod());
        order.setTransferNote(request.getTransferNote());
        order.setPaymentStatus("Pending");
        BigDecimal deliveryFee = "pickup".equalsIgnoreCase(request.getFulfilment())
                ? (request.getDeliveryFee() != null ? request.getDeliveryFee() : new BigDecimal("2500.00"))
                : BigDecimal.ZERO;
        order.setDeliveryFee(deliveryFee);

        BigDecimal discountPercentage = request.getDiscountPercentage() == null
                ? BigDecimal.ZERO : request.getDiscountPercentage();
        BigDecimal discountAmount = BigDecimal.ZERO;

        if (discountPercentage.compareTo(BigDecimal.ZERO) > 0) {
            requireDiscountPermission();

            BigDecimal serviceSubtotal = request.getItems().stream()
                    .map(item -> item.getQty().multiply(item.getPrice()))
                    .reduce(BigDecimal.ZERO, BigDecimal::add);

            discountAmount = serviceSubtotal
                    .multiply(discountPercentage)
                    .divide(new BigDecimal("100"), 2, RoundingMode.HALF_UP);
        }

        order.setDiscountPercentage(discountPercentage);
        order.setDiscountAmount(discountAmount);
        order.setTotal(request.getTotal().subtract(discountAmount));
        order.setStatus("Received");
        order.setFullName(request.getFullName());
        order.setPhone(request.getPhone());
        order.setEmail(request.getEmail());
        order.setDescription(request.getDescription());
        order.setCreatedBy(request.getCreatedBy());
        order.setPlacedAt(LocalDateTime.now());
        order.setArchived(false);
        order.setLocked(true);
        order.setPrinted(false);

        List<OrderItem> items = request.getItems().stream().map(itemReq -> {
            OrderItem item = new OrderItem();
            item.setOrder(order);
            item.setName(itemReq.getName());
            item.setQty(itemReq.getQty());
            item.setUnit(itemReq.getUnit());
            item.setPrice(itemReq.getPrice());
            return item;
        }).collect(Collectors.toList());
        order.setItems(items);

        return orderRepository.save(order);
    }

    private void requireDiscountPermission() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        boolean allowed = authentication != null
                && authentication.isAuthenticated()
                && !(authentication instanceof AnonymousAuthenticationToken)
                && authentication.getAuthorities().stream().anyMatch(authority ->
                        authority.getAuthority().equals("ROLE_STAFF")
                                || authority.getAuthority().equals("ROLE_MANAGER")
                                || authority.getAuthority().equals("ROLE_ADMIN"));

        if (!allowed) {
            throw new AccessDeniedException("You do not have permission to apply a discount");
        }
    }

    public List<Order> getAllOrders() {
        return orderRepository.findAll();
    }

    // Public lookup for the Track Order page — returns null (controller maps to
    // 404) rather than throwing, since "not found" is an expected, routine result
    // here, not an error condition.
    public Order findByReference(String referenceId) {
        return orderRepository.findByReferenceId(referenceId).orElse(null);
    }

    public Order updateOrder(Long id, OrderUpdateRequest request) {
        Order order = orderRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Order not found"));

        if (request.getStatus() != null) order.setStatus(request.getStatus());
        if (request.getTotal() != null) order.setTotal(request.getTotal());
        if (request.getPaymentStatus() != null) order.setPaymentStatus(request.getPaymentStatus());
        if (request.getArchived() != null) order.setArchived(request.getArchived());
        if (request.getPrinted() != null) order.setPrinted(request.getPrinted());
        if (request.getPaymentMethod() != null) order.setPaymentMethod(request.getPaymentMethod());

        return orderRepository.save(order);
    }

    // Permanent removal — only called from the Admin-only History delete
    // action. Everything else (archive) is a soft, reversible-in-spirit
    // action that just hides a record from the Today's list.
    public void deleteOrder(Long id) {
        orderRepository.deleteById(id);
    }
}