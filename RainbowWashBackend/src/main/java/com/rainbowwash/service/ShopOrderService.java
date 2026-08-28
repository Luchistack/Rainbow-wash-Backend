package com.rainbowwash.service;

import com.rainbowwash.dto.ShopOrderRequest;
import com.rainbowwash.dto.ShopOrderUpdateRequest;
import com.rainbowwash.model.ShopOrder;
import com.rainbowwash.model.ShopOrderItem;
import com.rainbowwash.repository.ShopOrderRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class ShopOrderService {

    private final ShopOrderRepository shopOrderRepository;

    public ShopOrderService(ShopOrderRepository shopOrderRepository) {
        this.shopOrderRepository = shopOrderRepository;
    }

    public ShopOrder createShopOrder(ShopOrderRequest request) {
        ShopOrder order = new ShopOrder();
        order.setReferenceId("SHOP-" + UUID.randomUUID().toString().substring(0, 6).toUpperCase());
        order.setFullName(request.getFullName());
        order.setPhone(request.getPhone());
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
            return item;
        }).collect(Collectors.toList());
        order.setItems(items);

        return shopOrderRepository.save(order);
    }

    public List<ShopOrder> getAllShopOrders() {
        return shopOrderRepository.findAll();
    }

    public ShopOrder updateShopOrder(Long id, ShopOrderUpdateRequest request) {
        ShopOrder order = shopOrderRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Shop order not found"));

        if (request.getStatus() != null) order.setStatus(request.getStatus());
        if (request.getPaymentStatus() != null) order.setPaymentStatus(request.getPaymentStatus());
        if (request.getArchived() != null) order.setArchived(request.getArchived());
        if (request.getPrinted() != null) order.setPrinted(request.getPrinted());
        if (request.getPaymentMethod() != null) order.setPaymentMethod(request.getPaymentMethod());

        return shopOrderRepository.save(order);
    }
}