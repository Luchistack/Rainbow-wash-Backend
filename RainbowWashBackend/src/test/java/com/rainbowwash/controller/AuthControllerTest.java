package com.rainbowwash.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.rainbowwash.dto.LoginRequest;
import com.rainbowwash.model.UserRole;
import com.rainbowwash.model.User;
import com.rainbowwash.security.JwtTokenProvider;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import com.rainbowwash.repository.UserRepository;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(AuthController.class)
@AutoConfigureMockMvc(addFilters = false)
class AuthControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private AuthenticationManager authenticationManager;

    @MockBean
    private JwtTokenProvider tokenProvider;

    @MockBean
    private UserRepository userRepository;

    @MockBean
    private PasswordEncoder passwordEncoder;

    @Test
    @DisplayName("Successfully authenticate user and return JWT token")
    void testAuthenticateUser_Success() throws Exception {
        LoginRequest request = new LoginRequest();
        request.setEmail("admin-test@example.com");
//        request.setRole(UserRole.ADMIN);
        request.setPassword("Password123!");

        Authentication authentication = org.mockito.Mockito.mock(Authentication.class);
        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenReturn(authentication);
        when(tokenProvider.generateToken(authentication)).thenReturn("mock-jwt-token-12345");

        User user = new User();
        user.setFullName("Admin User");
        user.setEmail("admin-test@example.com");
        user.setPassword("encoded-password");
        user.setRole(UserRole.ADMIN);
        when(userRepository.findByEmail("admin-test@example.com"))
                .thenReturn(java.util.Optional.of(user));

        mockMvc.perform(post("/api/auth/login").with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").value("mock-jwt-token-12345"))
                .andExpect(jsonPath("$.message").value("Login successful"));
    }

    @Test
    @DisplayName("Fail authentication on invalid credentials (BadCredentialsException)")
    void testAuthenticateUser_InvalidCredentials() throws Exception {
        LoginRequest request = new LoginRequest();
        request.setEmail("admin-test@example.com");
//        request.setRole(UserRole.ADMIN);
        request.setPassword("WrongPassword");

        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenThrow(new BadCredentialsException("Bad credentials"));

        mockMvc.perform(post("/api/auth/login").with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.error").value("Invalid email or password. Please try again."));
    }

    @Test
    @DisplayName("Fail validation when name or password is blank")
    void testAuthenticateUser_ValidationFailure() throws Exception {
        LoginRequest request = new LoginRequest();
        request.setEmail(""); // Blank name
        request.setPassword(null); // Missing role
        request.setPassword(""); // Blank password

        mockMvc.perform(post("/api/auth/login").with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }
}