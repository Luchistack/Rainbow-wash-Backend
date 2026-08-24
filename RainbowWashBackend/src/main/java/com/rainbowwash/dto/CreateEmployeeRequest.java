package com.rainbowwash.dto;

import com.rainbowwash.model.UserRole;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Data
@Getter
@Setter
public class CreateEmployeeRequest {
    @NotBlank(message = "Full name is required")
    private String fullName;

    @Email(message = "Invalid Email Format")
    @NotBlank(message = "Email is required")
    private String email;


    @NotNull(message = "Role is required")
    private UserRole role;
}
