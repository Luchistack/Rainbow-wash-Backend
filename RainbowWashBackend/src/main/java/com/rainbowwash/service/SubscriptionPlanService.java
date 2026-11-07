package com.rainbowwash.service;

import com.rainbowwash.dto.SubscriptionPlanRequest;
import com.rainbowwash.dto.SubscriptionPlanResponse;
import com.rainbowwash.model.SubscriptionPlan;
import com.rainbowwash.repository.SubscriptionPlanRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

@Service
public class SubscriptionPlanService implements CommandLineRunner {

    private final SubscriptionPlanRepository repository;

    public SubscriptionPlanService(SubscriptionPlanRepository repository) {
        this.repository = repository;
    }

    @Override
    public void run(String... args) {
        if (repository.count() != 0) {
            return;
        }

        repository.saveAll(List.of(
            new SubscriptionPlan(null, "Regular", new BigDecimal("50000"),
                "2 weeks", "Regular clothes only", null, true),
            new SubscriptionPlan(null, "Exclusive", new BigDecimal("100000"),
                "2 weeks", "Duvets and regular clothes", null, true),
            new SubscriptionPlan(null, "Premium", new BigDecimal("200000"),
                "1 month", "Everything, all laundry types", null, true)
        ));
    }

    @Transactional(readOnly = true)
    public List<SubscriptionPlanResponse> getActivePlans() {
        return repository.findByActiveTrue().stream().map(this::map).toList();
    }

    @Transactional(readOnly = true)
    public List<SubscriptionPlanResponse> getAllPlans() {
        return repository.findAll().stream().map(this::map).toList();
    }

    public SubscriptionPlanResponse createPlan(SubscriptionPlanRequest request) {
        SubscriptionPlan plan = new SubscriptionPlan();
        applyRequest(plan, request);
        return map(repository.save(plan));
    }

    public SubscriptionPlanResponse updatePlan(Long id, SubscriptionPlanRequest request) {
        SubscriptionPlan plan = repository.findById(id)
            .orElseThrow(() -> new RuntimeException("Subscription plan not found: " + id));
        applyRequest(plan, request);
        return map(repository.save(plan));
    }

    public void deletePlan(Long id) {
        if (!repository.existsById(id)) {
            throw new RuntimeException("Subscription plan not found: " + id);
        }
        repository.deleteById(id);
    }

    private void applyRequest(SubscriptionPlan plan, SubscriptionPlanRequest request) {
        plan.setName(request.getName().trim());
        plan.setPrice(request.getPrice());
        plan.setDuration(request.getDuration().trim());
        plan.setCovers(request.getCovers().trim());
        plan.setKgAllowance(request.getKgAllowance());
        plan.setActive(request.isActive());
    }

    private SubscriptionPlanResponse map(SubscriptionPlan plan) {
        return new SubscriptionPlanResponse(
            plan.getId(), plan.getName(), plan.getPrice(),
            plan.getDuration(), plan.getCovers(), plan.getKgAllowance(), plan.isActive()
        );
    }
}
