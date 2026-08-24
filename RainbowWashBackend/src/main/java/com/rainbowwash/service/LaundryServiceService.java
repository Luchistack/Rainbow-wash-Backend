package com.rainbowwash.service;

import com.rainbowwash.dto.LaundryServiceRequest;
import com.rainbowwash.dto.LaundryServiceResponse;
import com.rainbowwash.model.LaundryService;
import com.rainbowwash.repository.LaundryServiceRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class LaundryServiceService {

    private final LaundryServiceRepository serviceRepository;

    public LaundryServiceService(LaundryServiceRepository serviceRepository) {
        this.serviceRepository = serviceRepository;
    }

    public LaundryServiceResponse createService(LaundryServiceRequest request) {
        if (serviceRepository.existsByName(request.getName())) {
            throw new RuntimeException("Service with this name already exists");
        }
        LaundryService service = new LaundryService();
        service.setName(request.getName());
        service.setDescription(request.getDescription());
        service.setPrice(request.getPrice());
        service.setCategory(request.getCategory());
        service.setStock(request.getStock());
        service.setAvailable(request.isAvailable());

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
        service.setDescription(request.getDescription());
        service.setPrice(request.getPrice());
        service.setCategory(request.getCategory());
        service.setStock(request.getStock());
        service.setAvailable(request.isAvailable());

        LaundryService updated = serviceRepository.save(service);
        return mapToResponse(updated);
    }

    public void deleteService(Long id) {
        serviceRepository.deleteById(id);
    }

    private LaundryServiceResponse mapToResponse(LaundryService service) {
        return new LaundryServiceResponse(
                service.getId(),
                service.getName(),
                service.getDescription(),
                service.getPrice(),
                service.getCategory(),
                service.getStock(),
                service.isAvailable()
        );
    }
}