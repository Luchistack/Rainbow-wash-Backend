package com.rainbowwash.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.rainbowwash.repository.OrderRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class OrderValidationIntegrationTest {
    @Autowired private MockMvc mockMvc;
    @Autowired private OrderRepository orderRepository;
    @Autowired private ObjectMapper objectMapper;

    @BeforeEach
    void clearOrders() {
        orderRepository.deleteAll();
    }

    @Test
    void validOrderIsValidatedAndPersistedToH2() throws Exception {
        String payload = """
                {
                  "items":[{"name":"Wash & Dry","qty":2,"unit":"kg","price":1500}],
                  "fulfilment":"dropoff","address":"12 Test Street",
                  "preferredDate":"2026-10-20","preferredTime":"10:00",
                  "paymentMethod":"bank","total":3000,
                  "fullName":"Test Customer","phone":"08030001122",
                  "email":"customer@example.com"
                }
                """;

        mockMvc.perform(post("/api/orders")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(payload))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.referenceId").isNotEmpty())
                .andExpect(jsonPath("$.items[0].name").value("Wash & Dry"))
                .andExpect(jsonPath("$.status").value("Received"));

        org.junit.jupiter.api.Assertions.assertEquals(1, orderRepository.count());
    }

    @Test
    void invalidEmailIsRejectedAndNothingIsPersisted() throws Exception {
        String payload = """
                {
                  "items":[{"name":"Wash & Dry","qty":2,"unit":"kg","price":1500}],
                  "fulfilment":"dropoff","paymentMethod":"bank","total":3000,
                  "fullName":"Test Customer","phone":"08030001122",
                  "email":"customer@example.org"
                }
                """;

        mockMvc.perform(post("/api/orders")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(payload))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.fieldErrors.email").exists());

        org.junit.jupiter.api.Assertions.assertEquals(0, orderRepository.count());
    }

    @Test
    void invalidItemQuantityIsRejected() throws Exception {
        String payload = """
                {
                  "items":[{"name":"Wash & Dry","qty":0,"unit":"kg","price":1500}],
                  "paymentMethod":"bank","total":3000,
                  "fullName":"Test Customer","phone":"08030001122"
                }
                """;

        mockMvc.perform(post("/api/orders")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(payload))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.fieldErrors['items[0].qty']").exists());

        org.junit.jupiter.api.Assertions.assertEquals(0, orderRepository.count());
    }
}
