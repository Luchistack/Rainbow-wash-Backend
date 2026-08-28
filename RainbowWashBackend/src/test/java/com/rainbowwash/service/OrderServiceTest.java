package com.rainbowwash.service;

import com.rainbowwash.dto.OrderItemRequest;
import com.rainbowwash.dto.OrderRequest;
import com.rainbowwash.dto.OrderUpdateRequest;
import com.rainbowwash.model.Order;
import com.rainbowwash.repository.OrderRepository;
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

    @InjectMocks
    private OrderService orderService;

    private OrderRequest orderRequest;

    @BeforeEach
    void setUp() {
        OrderItemRequest item1 = new OrderItemRequest();
        item1.setName("Self Wash, Wash & Dry");
        item1.setQty(new BigDecimal("3"));
        item1.setUnit("kg");
        item1.setPrice(new BigDecimal("1500"));

        OrderItemRequest item2 = new OrderItemRequest();
        item2.setName("Suit (2-piece) (Regular)");
        item2.setQty(new BigDecimal("1"));
        item2.setUnit("");
        item2.setPrice(new BigDecimal("5000"));

        orderRequest = new OrderRequest();
        orderRequest.setItems(List.of(item1, item2));
        orderRequest.setFulfilment("dropoff");
        orderRequest.setPaymentMethod("bank");
        orderRequest.setTotal(new BigDecimal("9500")); // (3 * 1500) + 5000
        orderRequest.setFullName("Faith Dike");
        orderRequest.setPhone("0803 000 1122");
    }

    @Test
    void createOrder_savesOrderWithGeneratedReferenceAndItems() {
        when(orderRepository.save(any(Order.class))).thenAnswer(invocation -> {
            Order saved = invocation.getArgument(0);
            saved.setId(100L);
            return saved;
        });

        Order result = orderService.createOrder(orderRequest);

        assertNotNull(result);
        assertEquals(100L, result.getId());
        assertTrue(result.getReferenceId().startsWith("LND-"));
        assertEquals("Faith Dike", result.getFullName());
        assertEquals("Received", result.getStatus());
        assertEquals("Pending", result.getPaymentStatus());
        assertTrue(result.isLocked());
        assertFalse(result.isPrinted());
        assertFalse(result.isArchived());
        assertEquals(0, new BigDecimal("9500").compareTo(result.getTotal()));
        assertEquals(2, result.getItems().size());

        verify(orderRepository, times(1)).save(any(Order.class));
    }

    @Test
    void createOrder_doesNotRequireAnUserAccount() {
        // No userRepository dependency exists at all any more — this test's mere
        // presence (and passing) proves an anonymous customer order works.
        when(orderRepository.save(any(Order.class))).thenAnswer(invocation -> invocation.getArgument(0));
        Order result = orderService.createOrder(orderRequest);
        assertNotNull(result);
    }

    @Test
    void updateOrder_appliesOnlyProvidedFields() {
        Order existing = new Order();
        existing.setId(100L);
        existing.setStatus("Received");
        existing.setTotal(new BigDecimal("9500"));
        existing.setPaymentStatus("Pending");
        existing.setArchived(false);
        existing.setPrinted(false);

        when(orderRepository.findById(100L)).thenReturn(Optional.of(existing));
        when(orderRepository.save(any(Order.class))).thenReturn(existing);

        OrderUpdateRequest update = new OrderUpdateRequest();
        update.setStatus("Washing");

        Order result = orderService.updateOrder(100L, update);

        assertEquals("Washing", result.getStatus());
        // Untouched fields stay as they were
        assertEquals(0, new BigDecimal("9500").compareTo(result.getTotal()));
        assertEquals("Pending", result.getPaymentStatus());
        verify(orderRepository, times(1)).save(existing);
    }

    @Test
    void updateOrder_throwsWhenOrderNotFound() {
        when(orderRepository.findById(999L)).thenReturn(Optional.empty());
        OrderUpdateRequest update = new OrderUpdateRequest();
        update.setStatus("Washing");

        assertThrows(RuntimeException.class, () -> orderService.updateOrder(999L, update));
        verify(orderRepository, never()).save(any(Order.class));
    }
}
