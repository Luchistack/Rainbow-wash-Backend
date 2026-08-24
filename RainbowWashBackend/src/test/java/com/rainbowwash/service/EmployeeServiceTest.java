package com.rainbowwash.service;

import com.rainbowwash.dto.CreateEmployeeRequest;
import com.rainbowwash.model.User;
import com.rainbowwash.model.UserRole;
import com.rainbowwash.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class EmployeeServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private EmployeeService employeeService;

    private CreateEmployeeRequest request;

    @BeforeEach
    void setUp() {
        request = new CreateEmployeeRequest();
        request.setFullName("John Doe");
        request.setEmail("john@rainbowwash.com");
        request.setRole(UserRole.STAFF);
    }

    @Test
    void testProvisionEmployeeSuccess() {
        when(userRepository.existsByEmail(request.getEmail())).thenReturn(false);
        when(passwordEncoder.encode(any())).thenReturn("encodedPassword");
        when(userRepository.save(any(User.class))).thenAnswer(invocation -> invocation.getArgument(0));

        employeeService.provisionEmployee(request);

        verify(userRepository, times(1)).existsByEmail(request.getEmail());
        verify(userRepository, times(1)).save(any(User.class));
    }

    @Test
    void testProvisionEmployeeEmailExistsThrowsException() {
        when(userRepository.existsByEmail(request.getEmail())).thenReturn(true);

        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            employeeService.provisionEmployee(request);
        });

        assertEquals("An account with this email already exist.", exception.getMessage());
        verify(userRepository, never()).save(any(User.class));
    }
}