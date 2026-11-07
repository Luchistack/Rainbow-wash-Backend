package com.rainbowwash.repository;

import com.rainbowwash.model.SubscriptionPurchase;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SubscriptionPurchaseRepository extends JpaRepository<SubscriptionPurchase, Long> {
    boolean existsByReferenceId(String referenceId);
}
