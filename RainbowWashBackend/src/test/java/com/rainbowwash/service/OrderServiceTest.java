package com.rainbowwash.service;

import com.rainbowwash.dto.OrderItemRequest;
import com.rainbowwash.dto.OrderRequest;
import com.rainbowwash.dto.OrderResponse;
import com.rainbowwash.model.LaundryService;
import com.rainbowwash.model.Order;
import com.rainbowwash.model.OrderStatus;
import com.rainbowwash.model.User;
import com.rainbowwash.repository.LaundryServiceRepository;
import com.rainbowwash.repository.OrderRepository;
import com.rainbowwash.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class OrderServiceTest {

    @Mock
    private OrderRepository orderRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private LaundryServiceRepository laundryServiceRepository;

    @InjectMocks
    private OrderService orderService;

    private User mockUser;
    private LaundryService mockService1;
    private LaundryService mockService2;
    private OrderRequest orderRequest;

    @BeforeEach
    void setUp() {
        // Setup mock user
        mockUser = new User();
        mockUser.setId(1L);
        mockUser.setFullName("Faith Dike");
        mockUser.setEmail("faith@example.com");

        // Setup mock laundry services with distinct prices
        mockService1 = new LaundryService();
        mockService1.setId(10L);
        mockService1.setName("Dry Cleaning - Suit");
        mockService1.setPrice(new BigDecimal("3000.00"));

        mockService2 = new LaundryService();
        mockService2.setId(20L);
        mockService2.setName("Wash & Fold");
        mockService2.setPrice(new BigDecimal("1500.00"));

        // Setup an order request for 2 Suits and 3 Wash & Fold
        OrderItemRequest item1 = new OrderItemRequest();
        item1.setServiceId(10L);
        item1.setQuantity(2); // 2 * 3000 = 6000

        OrderItemRequest item2 = new OrderItemRequest();
        item2.setServiceId(20L);
        item2.setQuantity(3); // 3 * 1500 = 4500

        orderRequest = new OrderRequest();
        orderRequest.setUserId(1L);
        orderRequest.setDeliveryAddress("10 Semicolon Way, Yaba");
        orderRequest.setItems(List.of(item1, item2));
    }

    @Test
    void createOrder_ShouldCalculateCorrectTotalAndSave() {
        // Arrange: Mock repository responses
        when(userRepository.findById(1L)).thenReturn(Optional.of(mockUser));
        when(laundryServiceRepository.findById(10L)).thenReturn(Optional.of(mockService1));
        when(laundryServiceRepository.findById(20L)).thenReturn(Optional.of(mockService2));

        // When orderRepository.save is called, return the exact object that was passed to it
        when(orderRepository.save(any(Order.class))).thenAnswer(invocation -> {
            Order savedOrder = invocation.getArgument(0);
            savedOrder.setId(100L); // Mock generated ID
            return savedOrder;
        });

        // Act
        OrderResponse response = orderService.createOrder(orderRequest);

        // Assert
        assertNotNull(response);
        assertEquals(100L, response.getId());
        assertEquals("Faith Dike", response.getCustomerName());
        assertEquals(OrderStatus.PENDING, response.getStatus());
        assertEquals("10 Semicolon Way, Yaba", response.getDeliveryAddress());

        // Assert calculations: (2 * 3000) + (3 * 1500) = 6000 + 4500 = 10500
        BigDecimal expectedTotal = new BigDecimal("10500.00");
        assertEquals(0, expectedTotal.compareTo(response.getTotalAmount()), "Total amount calculation is incorrect");

        assertEquals(2, response.getItems().size());

        verify(orderRepository, times(1)).save(any(Order.class));
    }

    @Test
    void createOrder_ShouldThrowExceptionIfUserNotFound() {
        // Arrange
        when(userRepository.findById(1L)).thenReturn(Optional.empty());

        // Act & Assert
        Exception exception = assertThrows(RuntimeException.class, () -> {
            orderService.createOrder(orderRequest);
        });

        assertEquals("User not found", exception.getMessage());
        verify(orderRepository, never()).save(any(Order.class)); // Ensure it never tries to save
    }

    @Test
    void updateOrderStatus_ShouldUpdateAndReturn() {
        // Arrange
        Order existingOrder = new Order();
        existingOrder.setId(100L);
        existingOrder.setStatus(OrderStatus.PENDING);
        existingOrder.setUser(mockUser);
        existingOrder.setTotalAmount(new BigDecimal("5000"));
        existingOrder.setOrderItems(List.of());

        when(orderRepository.findById(100L)).thenReturn(Optional.of(existingOrder));
        when(orderRepository.save(any(Order.class))).thenReturn(existingOrder);

        // Act
        OrderResponse response = orderService.updateOrderStatus(100L, OrderStatus.PROCESSING);

        // Assert
        assertEquals(OrderStatus.PROCESSING, response.getStatus());
        verify(orderRepository, times(1)).save(existingOrder);
    }
}