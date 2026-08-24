package com.rainbowwash.controller;

import com.rainbowwash.dto.CreateEmployeeRequest;
import com.rainbowwash.dto.EmployeeSummary;
import com.rainbowwash.service.EmployeeService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

// Everything here is already locked to ADMIN-only by SecurityConfig
// (.requestMatchers("/api/admin/**").hasRole("ADMIN")), so no extra
// @PreAuthorize is needed on the individual methods.
@RestController
@RequestMapping("/api/admin")
public class AdminController {

    private final EmployeeService employeeService;

    public AdminController(EmployeeService employeeService) {
        this.employeeService = employeeService;
    }

    @PostMapping("/create-employee")
    public ResponseEntity<?> createEmployee(@Valid @RequestBody CreateEmployeeRequest request) {
        try {
            String tempPassword = employeeService.provisionEmployee(request);
            return ResponseEntity.ok(Map.of(
                    "message", "Account created for " + request.getFullName(),
                    "email", request.getEmail(),
                    "tempPassword", tempPassword
            ));
        } catch (RuntimeException e) {
            // e.g. "An account with this email already exist."
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("error", e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Failed to create employee: " + e.getMessage()));
        }
    }

    @GetMapping("/employees")
    public ResponseEntity<List<EmployeeSummary>> getEmployees() {
        return ResponseEntity.ok(employeeService.listEmployees());
    }

    @PostMapping("/reset-password/{id}")
    public ResponseEntity<?> resetPassword(@PathVariable Long id) {
        try {
            String tempPassword = employeeService.resetPassword(id);
            return ResponseEntity.ok(Map.of(
                    "message", "Password reset successfully.",
                    "tempPassword", tempPassword
            ));
        } catch (RuntimeException e) {
            // e.g. "User not found"
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("error", e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Failed to reset password: " + e.getMessage()));
        }
    }
}