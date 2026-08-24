package com.rainbowwash.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class EmployeeSummary {
    private Long id;
    private String fullName;
    private String email;
    private String role;
}