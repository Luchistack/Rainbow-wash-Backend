package com.rainbowwash.service;

import com.rainbowwash.dto.OrderRequest;
import com.rainbowwash.dto.OrderResponse;
import com.rainbowwash.model.*;
import com.rainbowwash.repository.LaundryServiceRepository;
import com.rainbowwash.repository.OrderRepository;
import com.rainbowwash.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class OrderService {

    private final OrderRepository orderRepository;
    private final UserRepository userRepository;
    private final LaundryServiceRepository laundryServiceRepository;

    public OrderService(OrderRepository orderRepository, UserRepository userRepository, LaundryServiceRepository laundryServiceRepository) {
        this.orderRepository = orderRepository;
        this.userRepository = userRepository;
        this.laundryServiceRepository = laundryServiceRepository;
    }

    @Transactional
    public OrderResponse createOrder(OrderRequest request) {
        User user = userRepository.findById(request.getUserId())
                .orElseThrow(() -> new RuntimeException("User not found"));

        Order order = new Order();
        order.setUser(user);
        order.setDeliveryAddress(request.getDeliveryAddress());
        order.setStatus(OrderStatus.PENDING);

        BigDecimal totalAmount = BigDecimal.ZERO;

        List<OrderItem> orderItems = request.getItems().stream().map(itemReq -> {
            LaundryService service = laundryServiceRepository.findById(itemReq.getServiceId())
                    .orElseThrow(() -> new RuntimeException("Laundry service not found: " + itemReq.getServiceId()));

            BigDecimal subTotal = service.getPrice().multiply(BigDecimal.valueOf(itemReq.getQuantity()));

            OrderItem item = new OrderItem();
            item.setOrder(order);
            item.setLaundryService(service);
            item.setQuantity(itemReq.getQuantity());
            item.setSubTotal(subTotal);

            return item;
        }).collect(Collectors.toList());

        for (OrderItem item : orderItems) {
            totalAmount = totalAmount.add(item.getSubTotal());
        }

        order.setOrderItems(orderItems);
        order.setTotalAmount(totalAmount);

        Order savedOrder = orderRepository.save(order);
        return mapToResponse(savedOrder);
    }

    public List<OrderResponse> getAllOrders() {
        return orderRepository.findAll().stream().map(this::mapToResponse).collect(Collectors.toList());
    }

    public List<OrderResponse> getOrdersByUser(Long userId) {
        return orderRepository.findByUserId(userId).stream().map(this::mapToResponse).collect(Collectors.toList());
    }

    public OrderResponse updateOrderStatus(Long orderId, OrderStatus status) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new RuntimeException("Order not found"));
        order.setStatus(status);
        return mapToResponse(orderRepository.save(order));
    }

    private OrderResponse mapToResponse(Order order) {
        OrderResponse res = new OrderResponse();
        res.setId(order.getId());
        res.setUserId(order.getUser().getId());
        res.setCustomerName(order.getUser().getFullName());
        res.setTotalAmount(order.getTotalAmount());
        res.setStatus(order.getStatus());
        res.setDeliveryAddress(order.getDeliveryAddress());
        res.setCreatedAt(order.getCreatedAt());

        List<OrderResponse.ItemDetail> details = order.getOrderItems().stream().map(i -> {
            OrderResponse.ItemDetail detail = new OrderResponse.ItemDetail();
            detail.setServiceName(i.getLaundryService().getName());
            detail.setQuantity(i.getQuantity());
            detail.setSubTotal(i.getSubTotal());
            return detail;
        }).collect(Collectors.toList());

        res.setItems(details);
        return res;
    }
}