package com.rainbowwash.service;

import com.rainbowwash.dto.CustomerSuggestionResponse;
import com.rainbowwash.model.CleaningBooking;
import com.rainbowwash.model.Order;
import com.rainbowwash.model.ShopOrder;
import com.rainbowwash.model.SubscriptionPurchase;
import com.rainbowwash.repository.CleaningBookingRepository;
import com.rainbowwash.repository.OrderRepository;
import com.rainbowwash.repository.ShopOrderRepository;
import com.rainbowwash.repository.SubscriptionPurchaseRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

@Service
public class CustomerSearchService {

    private final OrderRepository orderRepository;
    private final CleaningBookingRepository cleaningBookingRepository;
    private final ShopOrderRepository shopOrderRepository;
    private final SubscriptionPurchaseRepository subscriptionPurchaseRepository;

    public CustomerSearchService(
            OrderRepository orderRepository,
            CleaningBookingRepository cleaningBookingRepository,
            ShopOrderRepository shopOrderRepository,
            SubscriptionPurchaseRepository subscriptionPurchaseRepository) {
        this.orderRepository = orderRepository;
        this.cleaningBookingRepository = cleaningBookingRepository;
        this.shopOrderRepository = shopOrderRepository;
        this.subscriptionPurchaseRepository = subscriptionPurchaseRepository;
    }

    @Transactional(readOnly = true)
    public List<CustomerSuggestionResponse> search(String query) {
        String normalizedQuery = normalize(query);

        if (normalizedQuery.length() < 2) {
            return List.of();
        }

        String normalizedPhoneQuery = normalizePhone(query);
        Map<String, CustomerSuggestionResponse> customers = new LinkedHashMap<>();

        for (Order order : orderRepository.findAll()) {
            addCustomer(
                    customers,
                    order.getFullName(),
                    order.getPhone(),
                    order.getEmail(),
                    order.getAddress(),
                    order.getPlacedAt(),
                    normalizedQuery,
                    normalizedPhoneQuery
            );
        }

        for (CleaningBooking booking : cleaningBookingRepository.findAll()) {
            addCustomer(
                    customers,
                    booking.getFullName(),
                    booking.getPhone(),
                    booking.getEmail(),
                    booking.getAddress(),
                    booking.getPlacedAt(),
                    normalizedQuery,
                    normalizedPhoneQuery
            );
        }

        for (ShopOrder order : shopOrderRepository.findAll()) {
            addCustomer(
                    customers,
                    order.getFullName(),
                    order.getPhone(),
                    null,
                    null,
                    order.getPlacedAt(),
                    normalizedQuery,
                    normalizedPhoneQuery
            );
        }

        for (SubscriptionPurchase purchase : subscriptionPurchaseRepository.findAll()) {
            addCustomer(
                    customers,
                    purchase.getCustomerName(),
                    purchase.getCustomerPhone(),
                    null,
                    null,
                    purchase.getPlacedAt(),
                    normalizedQuery,
                    normalizedPhoneQuery
            );
        }

        return customers.values().stream()
                .sorted(Comparator
                        .comparing(
                                CustomerSuggestionResponse::getLastActivity,
                                Comparator.nullsLast(Comparator.reverseOrder())
                        )
                        .thenComparing(
                                CustomerSuggestionResponse::getFullName,
                                Comparator.nullsLast(String.CASE_INSENSITIVE_ORDER)
                        ))
                .limit(10)
                .toList();
    }

    private void addCustomer(
            Map<String, CustomerSuggestionResponse> customers,
            String fullName,
            String phone,
            String email,
            String address,
            LocalDateTime lastActivity,
            String query,
            String phoneQuery) {

        if (isBlank(fullName) && isBlank(phone)) {
            return;
        }

        String normalizedName = normalize(fullName);
        String normalizedPhone = normalizePhone(phone);

        boolean nameMatches = normalizedName.contains(query);
        boolean phoneMatches = !phoneQuery.isBlank()
                && normalizedPhone.contains(phoneQuery);

        if (!nameMatches && !phoneMatches) {
            return;
        }

        String identityKey = !normalizedPhone.isBlank()
                ? "phone:" + normalizedPhone
                : "name:" + normalizedName;

        CustomerSuggestionResponse existing = customers.get(identityKey);

        if (existing == null) {
            customers.put(
                    identityKey,
                    new CustomerSuggestionResponse(
                            trimToNull(fullName),
                            trimToNull(phone),
                            trimToNull(email),
                            trimToNull(address),
                            lastActivity
                    )
            );
            return;
        }

        if (isBlank(existing.getFullName()) && !isBlank(fullName)) {
            existing.setFullName(fullName.trim());
        }

        if (isBlank(existing.getPhone()) && !isBlank(phone)) {
            existing.setPhone(phone.trim());
        }

        if (isBlank(existing.getEmail()) && !isBlank(email)) {
            existing.setEmail(email.trim());
        }

        if (isBlank(existing.getAddress()) && !isBlank(address)) {
            existing.setAddress(address.trim());
        }

        if (lastActivity != null
                && (existing.getLastActivity() == null
                || lastActivity.isAfter(existing.getLastActivity()))) {
            existing.setLastActivity(lastActivity);
        }
    }

    private String normalize(String value) {
        return value == null
                ? ""
                : value.trim()
                .toLowerCase(Locale.ROOT)
                .replaceAll("\\s+", " ");
    }

    private String normalizePhone(String value) {
        return value == null
                ? ""
                : value.replaceAll("[^0-9]", "");
    }

    private boolean isBlank(String value) {
        return value == null || value.trim().isEmpty();
    }

    private String trimToNull(String value) {
        return isBlank(value) ? null : value.trim();
    }
}
