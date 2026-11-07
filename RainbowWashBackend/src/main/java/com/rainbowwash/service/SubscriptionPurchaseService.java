package com.rainbowwash.service;

import com.rainbowwash.dto.SubscriptionPurchaseRequest;
import com.rainbowwash.dto.SubscriptionPurchaseResponse;
import com.rainbowwash.model.SubscriptionPlan;
import com.rainbowwash.model.SubscriptionPurchase;
import com.rainbowwash.model.User;
import com.rainbowwash.repository.SubscriptionPlanRepository;
import com.rainbowwash.repository.SubscriptionPurchaseRepository;
import com.rainbowwash.repository.UserRepository;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Comparator;
import java.util.List;
import java.util.UUID;

@Service
public class SubscriptionPurchaseService {

    private static final BigDecimal VAT_RATE = new BigDecimal("0.0750");

    private final SubscriptionPurchaseRepository purchaseRepository;
    private final SubscriptionPlanRepository planRepository;
    private final UserRepository userRepository;

    public SubscriptionPurchaseService(
            SubscriptionPurchaseRepository purchaseRepository,
            SubscriptionPlanRepository planRepository,
            UserRepository userRepository) {
        this.purchaseRepository = purchaseRepository;
        this.planRepository = planRepository;
        this.userRepository = userRepository;
    }

    @Transactional
    public SubscriptionPurchaseResponse createPurchase(SubscriptionPurchaseRequest request) {
        if (request.getExpiryDate().isBefore(request.getStartDate())
                || request.getExpiryDate().isEqual(request.getStartDate())) {
            throw new ResponseStatusException(
                HttpStatus.BAD_REQUEST,
                "Subscription expiry date must be after the start date"
            );
        }

        SubscriptionPlan plan = planRepository.findById(request.getPlanId())
            .orElseThrow(() -> new ResponseStatusException(
                HttpStatus.NOT_FOUND, "Subscription plan not found"
            ));

        if (!plan.isActive()) {
            throw new ResponseStatusException(
                HttpStatus.BAD_REQUEST, "This subscription plan is not active"
            );
        }

        if (plan.getKgAllowance() == null
                || plan.getKgAllowance().compareTo(BigDecimal.ZERO) <= 0) {
            throw new ResponseStatusException(
                HttpStatus.BAD_REQUEST,
                "This plan has no KG allowance configured. Ask an Admin or Manager to configure it first."
            );
        }

        Authentication authentication =
            SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null || !authentication.isAuthenticated()
                || authentication.getName() == null) {
            throw new ResponseStatusException(
                HttpStatus.UNAUTHORIZED, "Authenticated staff identity is required"
            );
        }

        User creator = userRepository.findByEmail(authentication.getName())
            .orElseThrow(() -> new ResponseStatusException(
                HttpStatus.UNAUTHORIZED, "Authenticated staff account was not found"
            ));

        BigDecimal subtotal = plan.getPrice().setScale(2, RoundingMode.HALF_UP);
        BigDecimal vatAmount = subtotal.multiply(VAT_RATE)
            .setScale(2, RoundingMode.HALF_UP);
        BigDecimal total = subtotal.add(vatAmount)
            .setScale(2, RoundingMode.HALF_UP);

        SubscriptionPurchase purchase = new SubscriptionPurchase();
        purchase.setReferenceId(generateReference());
        purchase.setCustomerName(request.getCustomerName().trim());
        purchase.setCustomerPhone(request.getCustomerPhone().trim());
        purchase.setPlanId(plan.getId());
        purchase.setPlanName(plan.getName());
        purchase.setPlanDuration(plan.getDuration());
        purchase.setPlanCovers(plan.getCovers());
        purchase.setKgAllowance(plan.getKgAllowance());
        purchase.setSubtotal(subtotal);
        purchase.setVatRate(VAT_RATE);
        purchase.setVatAmount(vatAmount);
        purchase.setTotal(total);
        purchase.setStartDate(request.getStartDate());
        purchase.setExpiryDate(request.getExpiryDate());
        purchase.setCreatedByName(creator.getFullName());
        purchase.setCreatedByEmail(creator.getEmail());
        purchase.setPaymentMethod(normalizePaymentMethod(request.getPaymentMethod()));
        purchase.setPaymentStatus("Pending");
        purchase.setPrinted(false);

        return map(purchaseRepository.save(purchase));
    }

    @Transactional(readOnly = true)
    public List<SubscriptionPurchaseResponse> getAllPurchases() {
        return purchaseRepository.findAll().stream()
            .sorted(Comparator.comparing(
                SubscriptionPurchase::getPlacedAt,
                Comparator.nullsLast(Comparator.reverseOrder())
            ))
            .map(this::map)
            .toList();
    }

    @Transactional
    public SubscriptionPurchaseResponse updatePaymentStatus(
            Long id, String paymentStatus) {
        SubscriptionPurchase purchase = findPurchase(id);
        purchase.setPaymentStatus(normalizePaymentStatus(paymentStatus));
        return map(purchaseRepository.save(purchase));
    }

    @Transactional
    public SubscriptionPurchaseResponse markPrinted(Long id) {
        SubscriptionPurchase purchase = findPurchase(id);
        purchase.setPrinted(true);
        return map(purchaseRepository.save(purchase));
    }

    private SubscriptionPurchase findPurchase(Long id) {
        return purchaseRepository.findById(id)
            .orElseThrow(() -> new ResponseStatusException(
                HttpStatus.NOT_FOUND, "Subscription purchase not found"
            ));
    }

    private String generateReference() {
        String reference;
        do {
            reference = "SUB-" + UUID.randomUUID()
                .toString().replace("-", "").substring(0, 10).toUpperCase();
        } while (purchaseRepository.existsByReferenceId(reference));
        return reference;
    }

    private String normalizePaymentMethod(String method) {
        if ("transfer".equalsIgnoreCase(method)) {
            return "Transfer";
        }
        if ("pos".equalsIgnoreCase(method)) {
            return "POS";
        }
        throw new ResponseStatusException(
            HttpStatus.BAD_REQUEST, "Payment method must be Transfer or POS"
        );
    }

    private String normalizePaymentStatus(String status) {
        if (status == null) {
            throw new ResponseStatusException(
                HttpStatus.BAD_REQUEST, "Payment status is required"
            );
        }

        for (String allowed : List.of("Pending", "Sent", "Received", "Confirmed")) {
            if (allowed.equalsIgnoreCase(status.trim())) {
                return allowed;
            }
        }

        throw new ResponseStatusException(
            HttpStatus.BAD_REQUEST, "Invalid subscription payment status"
        );
    }

    private SubscriptionPurchaseResponse map(SubscriptionPurchase purchase) {
        return new SubscriptionPurchaseResponse(
            purchase.getId(),
            purchase.getReferenceId(),
            purchase.getCustomerName(),
            purchase.getCustomerPhone(),
            purchase.getPlanId(),
            purchase.getPlanName(),
            purchase.getPlanDuration(),
            purchase.getPlanCovers(),
            purchase.getKgAllowance(),
            purchase.getSubtotal(),
            purchase.getVatRate(),
            purchase.getVatAmount(),
            purchase.getTotal(),
            purchase.getStartDate(),
            purchase.getExpiryDate(),
            purchase.getCreatedByName(),
            purchase.getCreatedByEmail(),
            purchase.getPaymentMethod(),
            purchase.getPaymentStatus(),
            purchase.getPlacedAt(),
            purchase.isPrinted()
        );
    }
}
