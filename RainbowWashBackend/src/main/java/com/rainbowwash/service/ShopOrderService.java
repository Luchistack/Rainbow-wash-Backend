package com.rainbowwash.service;

import com.rainbowwash.dto.ShopOrderRequest;
import com.rainbowwash.dto.ShopOrderUpdateRequest;
import com.rainbowwash.model.Product;
import com.rainbowwash.model.ShopOrder;
import com.rainbowwash.model.ShopOrderItem;
import com.rainbowwash.repository.ProductRepository;
import com.rainbowwash.repository.ShopOrderRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class ShopOrderService {

    private final ShopOrderRepository shopOrderRepository;
    private final ProductRepository productRepository;

    public ShopOrderService(ShopOrderRepository shopOrderRepository,
                            ProductRepository productRepository) {
        this.shopOrderRepository = shopOrderRepository;
        this.productRepository = productRepository;
    }

    @Transactional
    public ShopOrder createShopOrder(ShopOrderRequest request) {
        ShopOrder order = new ShopOrder();
        order.setReferenceId("SHOP-" + UUID.randomUUID().toString().substring(0, 6).toUpperCase());
        order.setFullName(request.getFullName());
        order.setPhone(request.getPhone());
        order.setDescription(request.getDescription());
        order.setMode(request.getMode());
        order.setTotal(request.getTotal());
        order.setStatus("Received");
        order.setPaymentStatus("Pending");
        order.setCreatedBy(request.getCreatedBy());
        order.setPlacedAt(LocalDateTime.now());
        order.setArchived(false);
        order.setLocked(true);
        order.setPrinted(false);

        List<ShopOrderItem> items = request.getItems().stream().map(itemReq -> {
            ShopOrderItem item = new ShopOrderItem();
            item.setShopOrder(order);
            item.setName(itemReq.getName());
            item.setQty(itemReq.getQty());
            item.setPrice(itemReq.getPrice());
            item.setProductId(itemReq.getProductId());
            item.setStockDeducted(false);
            return item;
        }).collect(Collectors.toList());

        order.setItems(items);
        adjustInventory(items, true);
        return shopOrderRepository.save(order);
    }

    public List<ShopOrder> getAllShopOrders() {
        return shopOrderRepository.findAll();
    }

    @Transactional
    public ShopOrder updateShopOrder(Long id, ShopOrderUpdateRequest request) {
        ShopOrder order = shopOrderRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Shop order not found"));

        if (request.getStatus() != null) {
            boolean wasCancelled = "Cancelled".equalsIgnoreCase(order.getStatus());
            boolean willBeCancelled = "Cancelled".equalsIgnoreCase(request.getStatus());

            if (wasCancelled != willBeCancelled) {
                adjustInventory(order.getItems(), !willBeCancelled);
            }
            order.setStatus(request.getStatus());
        }

        if (request.getPaymentStatus() != null) order.setPaymentStatus(request.getPaymentStatus());
        if (request.getArchived() != null) order.setArchived(request.getArchived());
        if (request.getPrinted() != null) order.setPrinted(request.getPrinted());
        if (request.getPaymentMethod() != null) order.setPaymentMethod(request.getPaymentMethod());

        return shopOrderRepository.save(order);
    }

    @Transactional
    public void deleteShopOrder(Long id) {
        ShopOrder order = shopOrderRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Shop order not found"));

        adjustInventory(order.getItems(), false);
        shopOrderRepository.delete(order);
    }

    private void adjustInventory(List<ShopOrderItem> items, boolean deduct) {
        Map<Long, Integer> quantities = new TreeMap<>();

        for (ShopOrderItem item : items) {
            if (item.getProductId() == null) continue;

            boolean alreadyDeducted = Boolean.TRUE.equals(item.getStockDeducted());
            if (deduct == alreadyDeducted) continue;

            quantities.merge(item.getProductId(), item.getQty(), Integer::sum);
        }

        if (quantities.isEmpty()) return;

        Map<Long, Product> lockedProducts = new TreeMap<>();
        for (Long productId : quantities.keySet()) {
            Product product = productRepository.findByIdForUpdate(productId)
                    .orElseThrow(() -> new RuntimeException(
                            "Inventory product not found: " + productId));
            lockedProducts.put(productId, product);
        }

        if (deduct) {
            for (Map.Entry<Long, Integer> entry : quantities.entrySet()) {
                Product product = lockedProducts.get(entry.getKey());
                int requested = entry.getValue();

                if (product.getStock() < requested) {
                    throw new RuntimeException(
                            "Insufficient stock for " + product.getName()
                            + ". Available: " + product.getStock()
                            + ", requested: " + requested);
                }
            }
        }

        for (Map.Entry<Long, Integer> entry : quantities.entrySet()) {
            Product product = lockedProducts.get(entry.getKey());
            int quantity = entry.getValue();

            product.setStock(deduct
                    ? product.getStock() - quantity
                    : product.getStock() + quantity);

            productRepository.save(product);
        }

        for (ShopOrderItem item : items) {
            if (item.getProductId() == null) continue;

            boolean alreadyDeducted = Boolean.TRUE.equals(item.getStockDeducted());
            if (deduct != alreadyDeducted) {
                item.setStockDeducted(deduct);
            }
        }
    }
}
