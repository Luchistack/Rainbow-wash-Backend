package com.rainbowwash.service;

import com.rainbowwash.dto.LaundryServiceRequest;
import com.rainbowwash.dto.LaundryServiceResponse;
import com.rainbowwash.dto.ExpressPricingOptionRequest;
import com.rainbowwash.dto.ExpressPricingOptionResponse;
import com.rainbowwash.model.ExpressPricingOption;
import com.rainbowwash.model.LaundryService;
import com.rainbowwash.repository.LaundryServiceRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.ArrayList;
import java.util.Collections;
import java.util.stream.Collectors;

@org.springframework.transaction.annotation.Transactional
@Service
public class LaundryServiceService {

    private final LaundryServiceRepository serviceRepository;

    public LaundryServiceService(LaundryServiceRepository serviceRepository) {
        this.serviceRepository = serviceRepository;
    }

    public LaundryServiceResponse createService(LaundryServiceRequest request) {
        // Scoped to (name, category) rather than name alone — "Wash & Dry" needs
        // to exist once under Self Wash AND once under Staff Wash, for example.
        if (serviceRepository.existsByNameAndCategory(request.getName(), request.getCategory())) {
            throw new RuntimeException("A service with this name already exists in this category");
        }
        LaundryService service = new LaundryService();
        service.setName(request.getName());
        service.setDescription(request.getDescription());
        service.setPrice(request.getPrice());
        service.setDeepPrice(request.getDeepPrice());
        service.setRepairPrice(request.getRepairPrice());
        service.setTurnaround(request.getTurnaround());
        service.setPricingUnit(request.getPricingUnit());
        service.setCategory(request.getCategory());
        service.setStock(request.getStock());
        service.setAvailable(request.isAvailable());

        if ("Express".equalsIgnoreCase(request.getCategory())) {
            replaceExpressPricingOptions(service, request.getExpressPricingOptions());
        }

        LaundryService saved = serviceRepository.save(service);
        return mapToResponse(saved);
    }

    public List<LaundryServiceResponse> getAllServices() {
        return serviceRepository.findAll().stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    public LaundryServiceResponse updateService(Long id, LaundryServiceRequest request) {
        LaundryService service = serviceRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Service not found with id: " + id));

        service.setName(request.getName());
        if (request.getDescription() != null) {
            service.setDescription(request.getDescription());
        }
        service.setPrice(request.getPrice());
        service.setDeepPrice(request.getDeepPrice());
        service.setRepairPrice(request.getRepairPrice());
        service.setTurnaround(request.getTurnaround());
        service.setPricingUnit(request.getPricingUnit());
        service.setCategory(request.getCategory());
        service.setStock(request.getStock());
        service.setAvailable(request.isAvailable());

        if ("Express".equalsIgnoreCase(request.getCategory())
                && request.getExpressPricingOptions() != null) {
            replaceExpressPricingOptions(service, request.getExpressPricingOptions());
        }

        LaundryService updated = serviceRepository.save(service);
        return mapToResponse(updated);
    }

    public void deleteService(Long id) {
        serviceRepository.deleteById(id);
    }

    private void replaceExpressPricingOptions(
            LaundryService service,
            List<ExpressPricingOptionRequest> requests) {

        if (requests == null) {
            return;
        }

        if (service.getExpressPricingOptions() == null) {
            service.setExpressPricingOptions(new ArrayList<>());
        }

        service.getExpressPricingOptions().clear();

        for (ExpressPricingOptionRequest request : requests) {
            ExpressPricingOption option = new ExpressPricingOption();
            option.setService(service);
            option.setTurnaround(request.getTurnaround().trim());
            option.setPricingUnit(request.getPricingUnit().trim());
            option.setPrice(request.getPrice());
            option.setAvailable(request.isAvailable());
            service.getExpressPricingOptions().add(option);
        }
    }

    private List<ExpressPricingOptionResponse> mapExpressPricingOptions(
            LaundryService service) {

        if (service.getExpressPricingOptions() == null) {
            return Collections.emptyList();
        }

        return service.getExpressPricingOptions().stream()
                .map(option -> new ExpressPricingOptionResponse(
                        option.getId(),
                        option.getTurnaround(),
                        option.getPricingUnit(),
                        option.getPrice(),
                        option.isAvailable()
                ))
                .toList();
    }

    private LaundryServiceResponse mapToResponse(LaundryService service) {
        return new LaundryServiceResponse(
                service.getId(),
                service.getName(),
                service.getDescription(),
                service.getPrice(),
                service.getDeepPrice(),
                service.getRepairPrice(),
                service.getTurnaround(),
                service.getPricingUnit(),
                mapExpressPricingOptions(service),
                service.getCategory(),
                service.getStock(),
                service.isAvailable()
        );
    }
}