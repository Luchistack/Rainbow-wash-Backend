package com.rainbowwash.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.rainbowwash.dto.CreateEmployeeRequest;
import com.rainbowwash.model.UserRole;
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

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class AdminControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        userRepository.deleteAll();
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void testCreateEmployeeEndpointSuccess() throws Exception {
        CreateEmployeeRequest request = new CreateEmployeeRequest();
        request.setFullName("Jane Smith");
        request.setEmail("jane@rainbowwash.com");
        request.setRole(UserRole.MANAGER); // Pass UserRole enum

        mockMvc.perform(post("/api/admin/create-employee") // Updated endpoint URL to match controller
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk()) // Controller returns 200 OK with a string message
                .andExpect(content().string("Employee account created successfully. Temporary credentials sent."));
    }

    @Test
    void testCreateEmployeeUnauthorizedWithoutRole() throws Exception {
        CreateEmployeeRequest request = new CreateEmployeeRequest();
        request.setFullName("Jane Smith");
        request.setEmail("jane@rainbowwash.com");
        request.setRole(UserRole.MANAGER);

        mockMvc.perform(post("/api/admin/create-employee") // Updated endpoint URL
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isForbidden());
    }
}