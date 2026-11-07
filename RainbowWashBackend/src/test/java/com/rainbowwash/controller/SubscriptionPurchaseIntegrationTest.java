package com.rainbowwash.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.rainbowwash.model.SubscriptionPlan;
import com.rainbowwash.model.SubscriptionPurchase;
import com.rainbowwash.model.User;
import com.rainbowwash.model.UserRole;
import com.rainbowwash.repository.SubscriptionPlanRepository;
import com.rainbowwash.repository.SubscriptionPurchaseRepository;
import com.rainbowwash.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class SubscriptionPurchaseIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private SubscriptionPlanRepository planRepository;

    @Autowired
    private SubscriptionPurchaseRepository purchaseRepository;

    private SubscriptionPlan plan;

    @BeforeEach
    void setUp() {
        purchaseRepository.deleteAll();
        planRepository.deleteAll();
        userRepository.deleteAll();

        createUser("Staff Test", "staff.test@rainbowwash.com", UserRole.STAFF);
        createUser("Manager Test", "manager.test@rainbowwash.com", UserRole.MANAGER);
        createUser("Admin Test", "admin.test@rainbowwash.com", UserRole.ADMIN);

        plan = new SubscriptionPlan();
        plan.setName("Test Premium");
        plan.setPrice(new BigDecimal("100.00"));
        plan.setDuration("One month");
        plan.setCovers("Regular clothes and duvets");
        plan.setKgAllowance(new BigDecimal("25.00"));
        plan.setActive(true);
        plan = planRepository.save(plan);
    }

    private void createUser(String name, String email, UserRole role) {
        User user = new User();
        user.setFullName(name);
        user.setEmail(email);
        user.setPassword("test-password");
        user.setRole(role);
        user.setFirstLogin(false);
        userRepository.save(user);
    }

    private Map<String, Object> purchasePayload() {
        return Map.of(
            "customerName", "Test Customer",
            "customerPhone", "08012345678",
            "planId", plan.getId(),
            "startDate", "2026-11-10",
            "expiryDate", "2026-12-10",
            "paymentMethod", "Transfer"
        );
    }

    private Map<String, Object> planPayload() {
        return Map.of(
            "name", "New Test Plan",
            "price", 150.00,
            "duration", "One month",
            "covers", "Regular clothes",
            "kgAllowance", 30.00,
            "active", true
        );
    }

    @Test
    @WithMockUser(username = "staff.test@rainbowwash.com", roles = "STAFF")
    void staffCanCreatePurchaseWithStoredVatDatesAndCreator() throws Exception {
        mockMvc.perform(post("/api/subscription-purchases")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(purchasePayload())))
            .andExpect(status().isCreated())
            .andExpect(jsonPath("$.customerName").value("Test Customer"))
            .andExpect(jsonPath("$.planName").value("Test Premium"))
            .andExpect(jsonPath("$.kgAllowance").value(25.00))
            .andExpect(jsonPath("$.subtotal").value(100.00))
            .andExpect(jsonPath("$.vatRate").value(0.075))
            .andExpect(jsonPath("$.vatAmount").value(7.50))
            .andExpect(jsonPath("$.total").value(107.50))
            .andExpect(jsonPath("$.startDate").value("2026-11-10"))
            .andExpect(jsonPath("$.expiryDate").value("2026-12-10"))
            .andExpect(jsonPath("$.createdByName").value("Staff Test"))
            .andExpect(jsonPath("$.createdByEmail").value("staff.test@rainbowwash.com"))
            .andExpect(jsonPath("$.paymentMethod").value("Transfer"))
            .andExpect(jsonPath("$.paymentStatus").value("Pending"))
            .andExpect(jsonPath("$.printed").value(false));

        SubscriptionPurchase saved = purchaseRepository.findAll().get(0);
        assertEquals(new BigDecimal("7.50"), saved.getVatAmount());
        assertEquals(new BigDecimal("107.50"), saved.getTotal());
        assertEquals(LocalDate.of(2026, 11, 10), saved.getStartDate());
        assertEquals(LocalDate.of(2026, 12, 10), saved.getExpiryDate());
        assertEquals("Staff Test", saved.getCreatedByName());
    }

    @Test
    @WithMockUser(username = "manager.test@rainbowwash.com", roles = "MANAGER")
    void managerCanCreateSubscriptionPurchase() throws Exception {
        mockMvc.perform(post("/api/subscription-purchases")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(purchasePayload())))
            .andExpect(status().isCreated())
            .andExpect(jsonPath("$.createdByName").value("Manager Test"));
    }

    @Test
    @WithMockUser(username = "admin.test@rainbowwash.com", roles = "ADMIN")
    void adminCanCreateSubscriptionPurchase() throws Exception {
        mockMvc.perform(post("/api/subscription-purchases")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(purchasePayload())))
            .andExpect(status().isCreated())
            .andExpect(jsonPath("$.createdByName").value("Admin Test"));
    }

    @Test
    @WithMockUser(username = "staff.test@rainbowwash.com", roles = "STAFF")
    void staffCannotManageOrCreateSubscriptionPlans() throws Exception {
        mockMvc.perform(get("/api/subscription-plans/manage"))
            .andExpect(status().isForbidden());

        mockMvc.perform(post("/api/subscription-plans")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(planPayload())))
            .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(username = "manager.test@rainbowwash.com", roles = "MANAGER")
    void managerCanCreateSubscriptionPlan() throws Exception {
        mockMvc.perform(post("/api/subscription-plans")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(planPayload())))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.name").value("New Test Plan"))
            .andExpect(jsonPath("$.kgAllowance").value(30.00));
    }

    @Test
    @WithMockUser(username = "staff.test@rainbowwash.com", roles = "STAFF")
    void purchaseIsRejectedWhenPlanHasNoKgAllowance() throws Exception {
        plan.setKgAllowance(null);
        planRepository.save(plan);

        mockMvc.perform(post("/api/subscription-purchases")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(purchasePayload())))
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.message")
                .value(org.hamcrest.Matchers.containsString("KG allowance")));

        assertEquals(0, purchaseRepository.count());
    }

    @Test
    @WithMockUser(username = "staff.test@rainbowwash.com", roles = "STAFF")
    void purchaseRejectsExpiryDateThatIsNotAfterStartDate() throws Exception {
        Map<String, Object> payload = Map.of(
            "customerName", "Test Customer",
            "customerPhone", "08012345678",
            "planId", plan.getId(),
            "startDate", "2026-11-10",
            "expiryDate", "2026-11-10",
            "paymentMethod", "POS"
        );

        mockMvc.perform(post("/api/subscription-purchases")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(payload)))
            .andExpect(status().isBadRequest());

        assertEquals(0, purchaseRepository.count());
    }

    @Test
    @WithMockUser(username = "staff.test@rainbowwash.com", roles = "STAFF")
    void staffCanUpdatePurchasePaymentStatusAndPrintedFlag() throws Exception {
        mockMvc.perform(post("/api/subscription-purchases")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(purchasePayload())))
            .andExpect(status().isCreated());

        SubscriptionPurchase saved = purchaseRepository.findAll().get(0);

        mockMvc.perform(patch(
                    "/api/subscription-purchases/" + saved.getId() + "/payment-status")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(
                    Map.of("paymentStatus", "Received"))))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.paymentStatus").value("Received"));

        mockMvc.perform(patch(
                    "/api/subscription-purchases/" + saved.getId() + "/printed"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.printed").value(true));

        SubscriptionPurchase updated =
            purchaseRepository.findById(saved.getId()).orElseThrow();

        assertEquals("Received", updated.getPaymentStatus());
        assertTrue(updated.isPrinted());
    }
}
