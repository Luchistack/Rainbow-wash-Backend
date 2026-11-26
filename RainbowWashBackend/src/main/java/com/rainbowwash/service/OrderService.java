package com.rainbowwash.service;

import com.rainbowwash.dto.OrderItemRequest;
import com.rainbowwash.dto.OrderRequest;
import com.rainbowwash.dto.OrderUpdateRequest;
import com.rainbowwash.model.Order;
import com.rainbowwash.model.OrderItem;
import com.rainbowwash.repository.OrderRepository;
import com.rainbowwash.repository.ProductRepository;
import com.rainbowwash.model.Product;
import org.springframework.transaction.annotation.Transactional;
import java.util.Map;
import java.util.TreeMap;
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
    private final ProductRepository productRepository;

    public OrderService(OrderRepository orderRepository, ProductRepository productRepository) {
        this.orderRepository = orderRepository;
        this.productRepository = productRepository;
    }

    @Transactional
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
            item.setProductId(itemReq.getProductId());
            item.setStockDeducted(false);
            return item;
        }).collect(Collectors.toList());
        order.setItems(items);
        adjustInventory(items, true);

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

    @Transactional
    public Order updateOrder(Long id, OrderUpdateRequest request) {
        Order order = orderRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Order not found"));

        String previousFulfilment = order.getFulfilment();

        if (request.getFullName() != null) order.setFullName(request.getFullName());
        if (request.getPhone() != null) order.setPhone(request.getPhone());
        if (request.getEmail() != null) order.setEmail(request.getEmail());
        if (request.getDescription() != null) order.setDescription(request.getDescription());

        if (request.getFulfilment() != null) order.setFulfilment(request.getFulfilment());
        if (request.getAddress() != null) order.setAddress(request.getAddress());
        if (request.getPreferredDate() != null) order.setPreferredDate(request.getPreferredDate());
        if (request.getPreferredTime() != null) order.setPreferredTime(request.getPreferredTime());

        if (request.getPaymentMethod() != null) order.setPaymentMethod(request.getPaymentMethod());
        if (request.getTransferNote() != null) order.setTransferNote(request.getTransferNote());
        if (request.getPaymentStatus() != null) order.setPaymentStatus(request.getPaymentStatus());
        if (request.getStatus() != null) order.setStatus(request.getStatus());
        if (request.getArchived() != null) order.setArchived(request.getArchived());
        if (request.getPrinted() != null) order.setPrinted(request.getPrinted());

        boolean itemsChanged = request.getItems() != null;
        if (itemsChanged) {
            if (request.getItems().isEmpty()) {
                throw new IllegalArgumentException("An order must contain at least one item");
            }

            // Restore previously deducted items before replacing the lines.
            adjustInventory(order.getItems(), false);
            order.getItems().clear();

            for (OrderItemRequest itemRequest : request.getItems()) {
                OrderItem item = new OrderItem();
                item.setOrder(order);
                item.setName(itemRequest.getName());
                item.setQty(itemRequest.getQty());
                item.setUnit(itemRequest.getUnit());
                item.setPrice(itemRequest.getPrice());
                item.setProductId(itemRequest.getProductId());
                item.setStockDeducted(false);
                order.getItems().add(item);
            }
            adjustInventory(order.getItems(), true);
        }

        if (request.getDeliveryFee() != null) {
            order.setDeliveryFee(request.getDeliveryFee());
        } else if ("dropoff".equalsIgnoreCase(order.getFulfilment())) {
            order.setDeliveryFee(BigDecimal.ZERO);
        } else if ("pickup".equalsIgnoreCase(order.getFulfilment())
                && !"pickup".equalsIgnoreCase(previousFulfilment)) {
            order.setDeliveryFee(new BigDecimal("2500.00"));
        }

        BigDecimal discountPercentage = request.getDiscountPercentage() != null
                ? request.getDiscountPercentage()
                : (order.getDiscountPercentage() == null ? BigDecimal.ZERO : order.getDiscountPercentage());

        boolean discountChanged = request.getDiscountPercentage() != null;
        boolean deliveryChanged = request.getDeliveryFee() != null || request.getFulfilment() != null;
        boolean pricingComponentsChanged = itemsChanged || discountChanged || deliveryChanged;

        if (pricingComponentsChanged) {
            if (discountPercentage.compareTo(BigDecimal.ZERO) > 0) {
                requireDiscountPermission();
            }

            BigDecimal serviceSubtotal = order.getItems().stream()
                    .map(item -> item.getQty().multiply(item.getPrice()))
                    .reduce(BigDecimal.ZERO, BigDecimal::add);

            BigDecimal discountAmount = serviceSubtotal
                    .multiply(discountPercentage)
                    .divide(new BigDecimal("100"), 2, RoundingMode.HALF_UP);

            BigDecimal deliveryFee = order.getDeliveryFee() == null
                    ? BigDecimal.ZERO : order.getDeliveryFee();

            order.setDiscountPercentage(discountPercentage);
            order.setDiscountAmount(discountAmount);
            order.setTotal(serviceSubtotal.add(deliveryFee).subtract(discountAmount));
        } else {
            if (request.getTotal() != null) {
                order.setTotal(request.getTotal());
            }
            if (discountChanged) {
                order.setDiscountPercentage(discountPercentage);
            }
        }

        return orderRepository.save(order);
    }

    // Permanent removal — only called from the Admin-only History delete
    // action. Everything else (archive) is a soft, reversible-in-spirit
    // action that just hides a record from the Today's list.
    @Transactional
    public void deleteOrder(Long id) {
        Order order = orderRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Order not found"));
        adjustInventory(order.getItems(), false);
        orderRepository.delete(order);
    }

    /** Deducts or restores stock only for lines linked to Inventory products. */
    private void adjustInventory(List<OrderItem> items, boolean deduct) {
        Map<Long, Integer> quantities = new TreeMap<>();
        for (OrderItem item : items) {
            if (item.getProductId() == null) continue;
            if (deduct == item.isStockDeducted()) continue;
            int quantity;
            try {
                quantity = item.getQty().intValueExact();
            } catch (ArithmeticException ex) {
                throw new IllegalArgumentException(
                        "Inventory product quantities must be whole numbers: " + item.getName());
            }
            if (quantity <= 0) {
                throw new IllegalArgumentException("Inventory product quantity must be greater than zero");
            }
            quantities.merge(item.getProductId(), quantity, Math::addExact);
        }

        if (quantities.isEmpty()) return;

        Map<Long, Product> lockedProducts = new TreeMap<>();
        for (Long productId : quantities.keySet()) {
            Product product = productRepository.findByIdForUpdate(productId)
                    .orElseThrow(() -> new IllegalArgumentException(
                            "Inventory product not found: " + productId));
            lockedProducts.put(productId, product);
        }

        if (deduct) {
            for (Map.Entry<Long, Integer> entry : quantities.entrySet()) {
                Product product = lockedProducts.get(entry.getKey());
                if (product.getStock() < entry.getValue()) {
                    throw new IllegalArgumentException(
                            "Insufficient stock for " + product.getName()
                                    + ". Available: " + product.getStock()
                                    + ", requested: " + entry.getValue());
                }
            }
        }

        for (Map.Entry<Long, Integer> entry : quantities.entrySet()) {
            Product product = lockedProducts.get(entry.getKey());
            int quantity = entry.getValue();
            product.setStock(deduct ? product.getStock() - quantity : product.getStock() + quantity);
            productRepository.save(product);
        }

        for (OrderItem item : items) {
            if (item.getProductId() != null) {
                boolean shouldChange = deduct ? !item.isStockDeducted() : item.isStockDeducted();
                if (shouldChange) item.setStockDeducted(deduct);
            }
        }
    }
}