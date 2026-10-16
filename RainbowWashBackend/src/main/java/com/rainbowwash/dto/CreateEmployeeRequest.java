package com.rainbowwash.dto;

import com.rainbowwash.model.UserRole;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Data
@Getter
@Setter
public class CreateEmployeeRequest {
    @NotBlank(message = "Full name is required")
    @Size(max = 120, message = "Full name must be 120 characters or fewer")
    private String fullName;

    @Email(message = "Invalid Email Format")
    @NotBlank(message = "Email is required")
    @Size(max = 254, message = "Email must be 254 characters or fewer")
    @Pattern(regexp = "(?i)^[A-Z0-9.!#$%&'*+/=?^_`{|}~-]+@[A-Z0-9](?:[A-Z0-9-]*[A-Z0-9])?(?:\\.[A-Z0-9](?:[A-Z0-9-]*[A-Z0-9])?)*\\.com$", message = "Email must be valid and end in .com")
    private String email;


    @NotNull(message = "Role is required")
    private UserRole role;
}
