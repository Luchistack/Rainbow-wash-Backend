package com.rainbowwash.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Size;
import java.util.List;
import lombok.Data;
import java.math.BigDecimal;

@Data
public class LaundryServiceRequest {
    @NotBlank(message = "Service name is required")
    private String name;

    @Size(max = 1000, message = "Description must be 1000 characters or fewer")
    private String description;

    @NotNull(message = "Price is required")
    @DecimalMin(value = "0.0", inclusive = false, message = "Price must be greater than zero")
    private BigDecimal price;

    // Optional — only set for Dry Cleaning (deepPrice) and Shoe Care (deepPrice + repairPrice).
    @DecimalMin(value = "0.00", message = "Deep-cleaning price cannot be negative")
    private BigDecimal deepPrice;
    @DecimalMin(value = "0.00", message = "Repair price cannot be negative")
    private BigDecimal repairPrice;

    // Optional Express-only settings.
    @Size(max = 80, message = "Turnaround must be 80 characters or fewer")
    private String turnaround;

    // Legacy single-option fields retained for compatibility.
    @Size(max = 80, message = "Pricing unit must be 80 characters or fewer")
    private String pricingUnit;

    // New manually configured Express options. Null means preserve existing options on update.
    @Valid
    private List<ExpressPricingOptionRequest> expressPricingOptions;

    @NotBlank(message = "Category is required")
    @Size(max = 100, message = "Category must be 100 characters or fewer")
    private String category;

    @Min(value = 5, message = "New stock always starts at 5 units or more")
    private int stock = 5;

    private boolean available = true;
}