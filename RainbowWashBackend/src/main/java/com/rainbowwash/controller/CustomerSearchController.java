package com.rainbowwash.controller;

import com.rainbowwash.dto.CustomerSuggestionResponse;
import com.rainbowwash.service.CustomerSearchService;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/customers")
@PreAuthorize("hasAnyRole('STAFF', 'MANAGER', 'ADMIN')")
public class CustomerSearchController {

    private final CustomerSearchService customerSearchService;

    public CustomerSearchController(CustomerSearchService customerSearchService) {
        this.customerSearchService = customerSearchService;
    }

    @GetMapping("/search")
    public List<CustomerSuggestionResponse> searchCustomers(
            @RequestParam(name = "q", defaultValue = "") String query) {
        return customerSearchService.search(query);
    }
}
