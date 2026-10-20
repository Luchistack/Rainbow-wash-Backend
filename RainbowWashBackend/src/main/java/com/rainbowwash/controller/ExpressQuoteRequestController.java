package com.rainbowwash.controller;

import com.rainbowwash.model.ExpressQuoteRequest;
import com.rainbowwash.service.ExpressQuoteRequestService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;

@RestController
@RequestMapping("/api/express-quote-requests")
public class ExpressQuoteRequestController {
    private final ExpressQuoteRequestService service;

    public ExpressQuoteRequestController(ExpressQuoteRequestService service) {
        this.service = service;
    }

    public static class CreateRequest {
        @NotBlank @Size(max = 120) public String fullName;
        @NotBlank @Pattern(regexp = "^[+0-9() .-]{7,25}$") public String phone;
        @Email @Size(max = 254) public String email;
        @NotBlank @Size(max = 120) public String service;
        @NotBlank @Pattern(regexp = "^(same-day|next-day)$") public String turnaround;
        @NotBlank @Size(max = 2000) public String description;
    }

    public static class UpdateRequest {
        @DecimalMin(value = "0.01") public BigDecimal quotedPrice;
        @Pattern(regexp = "^(Pending Quote|Quoted|Confirmed|In Progress|Completed|Cancelled)$")
        public String status;
        public Boolean archived;
        public Boolean printed;
    }

    @PostMapping
    public ResponseEntity<ExpressQuoteRequest> create(@Valid @RequestBody CreateRequest body) {
        ExpressQuoteRequest request = new ExpressQuoteRequest();
        request.setFullName(body.fullName.trim());
        request.setPhone(body.phone.trim());
        request.setEmail(body.email == null ? null : body.email.trim());
        request.setService(body.service.trim());
        request.setTurnaround(body.turnaround);
        request.setDescription(body.description.trim());
        return ResponseEntity.ok(service.create(request));
    }

    @GetMapping
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<List<ExpressQuoteRequest>> findAll() {
        return ResponseEntity.ok(service.findAll());
    }

    @PatchMapping("/{id}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<ExpressQuoteRequest> update(
            @PathVariable Long id, @Valid @RequestBody UpdateRequest body) {
        return ResponseEntity.ok(service.update(
                id, body.quotedPrice, body.status, body.archived, body.printed));
    }
}
