package com.rainbowwash.service;

import com.rainbowwash.dto.LaundryServiceRequest;
import com.rainbowwash.dto.ExpressPricingOptionRequest;
import com.rainbowwash.dto.LaundryServiceResponse;
import com.rainbowwash.model.LaundryService;
import com.rainbowwash.model.ExpressPricingOption;
import com.rainbowwash.repository.LaundryServiceRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.List;
import java.util.ArrayList;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class LaundryServiceServiceTest {

    @Mock
    private LaundryServiceRepository serviceRepository;

    @InjectMocks
    private LaundryServiceService laundryServiceService;

    private LaundryServiceRequest createRequest() {
        LaundryServiceRequest request = new LaundryServiceRequest();
        request.setName("Express Laundry");
        request.setDescription("Fast laundry service");
        request.setPrice(new BigDecimal("2500"));
        request.setCategory("Express");
        request.setStock(5);
        request.setAvailable(true);
        request.setTurnaround("same-day");
        request.setPricingUnit("PER_KG");
        return request;
    }


    private ExpressPricingOptionRequest option(
            String turnaround,
            String pricingUnit,
            String price,
            boolean available
    ) {
        ExpressPricingOptionRequest option = new ExpressPricingOptionRequest();
        option.setTurnaround(turnaround);
        option.setPricingUnit(pricingUnit);
        option.setPrice(new BigDecimal(price));
        option.setAvailable(available);
        return option;
    }

    @Test
    void createServiceShouldSaveExpressPricingConfiguration() {
        LaundryServiceRequest request = createRequest();

        when(serviceRepository.existsByNameAndCategory(
                request.getName(), request.getCategory()
        )).thenReturn(false);

        when(serviceRepository.save(any(LaundryService.class)))
                .thenAnswer(invocation -> {
                    LaundryService saved = invocation.getArgument(0);
                    saved.setId(1L);
                    return saved;
                });

        LaundryServiceResponse response =
                laundryServiceService.createService(request);

        assertEquals("same-day", response.getTurnaround());
        assertEquals("PER_KG", response.getPricingUnit());
        assertEquals(new BigDecimal("2500"), response.getPrice());

        verify(serviceRepository).save(any(LaundryService.class));
    }

    @Test
    void updateServiceShouldUpdateExpressPricingConfiguration() {
        LaundryService existing = new LaundryService();
        existing.setId(1L);
        existing.setName("Express Laundry");
        existing.setPrice(new BigDecimal("2500"));
        existing.setCategory("Express");
        existing.setStock(5);
        existing.setAvailable(true);
        existing.setTurnaround("same-day");
        existing.setPricingUnit("PER_KG");

        LaundryServiceRequest request = createRequest();
        request.setTurnaround("next-day");
        request.setPricingUnit("PER_JOB");
        request.setPrice(new BigDecimal("5000"));

        when(serviceRepository.findById(1L)).thenReturn(Optional.of(existing));
        when(serviceRepository.save(any(LaundryService.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        LaundryServiceResponse response =
                laundryServiceService.updateService(1L, request);

        assertEquals("next-day", response.getTurnaround());
        assertEquals("PER_JOB", response.getPricingUnit());
        assertEquals(new BigDecimal("5000"), response.getPrice());
    }

    @Test
    void getAllServicesShouldReturnExpressPricingConfiguration() {
        LaundryService service = new LaundryService();
        service.setId(1L);
        service.setName("Express Laundry");
        service.setDescription("Fast laundry service");
        service.setPrice(new BigDecimal("2500"));
        service.setCategory("Express");
        service.setStock(5);
        service.setAvailable(true);
        service.setTurnaround("same-day");
        service.setPricingUnit("PER_KG");

        when(serviceRepository.findAll()).thenReturn(List.of(service));

        List<LaundryServiceResponse> responses =
                laundryServiceService.getAllServices();

        assertEquals(1, responses.size());
        assertEquals("same-day", responses.get(0).getTurnaround());
        assertEquals("PER_KG", responses.get(0).getPricingUnit());
    }

    @Test
    void createServiceShouldReturnMultipleExpressPricingOptions() {
        LaundryServiceRequest request = createRequest();
        request.setExpressPricingOptions(List.of(
                option("Same-day Express", "Per kg", "3000", true),
                option("Next-day Express", "Per item", "5000", true),
                option("Standard Express", "Per job", "7000", false)
        ));

        when(serviceRepository.existsByNameAndCategory(
                request.getName(), request.getCategory()
        )).thenReturn(false);

        when(serviceRepository.save(any(LaundryService.class)))
                .thenAnswer(invocation -> {
                    LaundryService saved = invocation.getArgument(0);
                    saved.setId(1L);
                    return saved;
                });

        LaundryServiceResponse response =
                laundryServiceService.createService(request);

        assertEquals(3, response.getExpressPricingOptions().size());
        assertEquals("Same-day Express",
                response.getExpressPricingOptions().get(0).getTurnaround());
        assertEquals(new BigDecimal("5000"),
                response.getExpressPricingOptions().get(1).getPrice());
        assertFalse(response.getExpressPricingOptions().get(2).isAvailable());
    }

    @Test
    void updateServiceShouldReplaceExpressPricingOptions() {
        LaundryService existing = new LaundryService();
        existing.setId(1L);
        existing.setName("Express Laundry");
        existing.setPrice(new BigDecimal("2500"));
        existing.setCategory("Express");
        existing.setStock(5);
        existing.setAvailable(true);

        LaundryServiceRequest request = createRequest();
        request.setExpressPricingOptions(List.of(
                option("New turnaround", "Per item", "6000", true),
                option("Weekend Express", "Per job", "9000", false)
        ));

        when(serviceRepository.findById(1L)).thenReturn(Optional.of(existing));
        when(serviceRepository.save(any(LaundryService.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        LaundryServiceResponse response =
                laundryServiceService.updateService(1L, request);

        assertEquals(2, response.getExpressPricingOptions().size());
        assertEquals("New turnaround",
                response.getExpressPricingOptions().get(0).getTurnaround());
        assertEquals("Weekend Express",
                response.getExpressPricingOptions().get(1).getTurnaround());
    }

    @Test
    void updateServiceShouldPreserveOptionsWhenListIsOmitted() {
        LaundryService existing = new LaundryService();
        existing.setId(1L);
        existing.setName("Express Laundry");
        existing.setPrice(new BigDecimal("2500"));
        existing.setCategory("Express");
        existing.setStock(5);
        existing.setAvailable(true);

        ExpressPricingOption existingOption = new ExpressPricingOption();
        existingOption.setService(existing);
        existingOption.setTurnaround("Same-day Express");
        existingOption.setPricingUnit("Per kg");
        existingOption.setPrice(new BigDecimal("3000"));
        existingOption.setAvailable(true);
        existing.setExpressPricingOptions(new ArrayList<>(List.of(existingOption)));

        LaundryServiceRequest request = createRequest();
        request.setExpressPricingOptions(null);

        when(serviceRepository.findById(1L)).thenReturn(Optional.of(existing));
        when(serviceRepository.save(any(LaundryService.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        LaundryServiceResponse response =
                laundryServiceService.updateService(1L, request);

        assertEquals(1, response.getExpressPricingOptions().size());
        assertEquals("Same-day Express",
                response.getExpressPricingOptions().get(0).getTurnaround());
    }

    @Test
    void updateServiceShouldRemoveAllOptionsWhenEmptyListIsSubmitted() {
        LaundryService existing = new LaundryService();
        existing.setId(1L);
        existing.setName("Express Laundry");
        existing.setPrice(new BigDecimal("2500"));
        existing.setCategory("Express");
        existing.setStock(5);
        existing.setAvailable(true);

        ExpressPricingOption existingOption = new ExpressPricingOption();
        existingOption.setService(existing);
        existingOption.setTurnaround("Same-day Express");
        existingOption.setPricingUnit("Per kg");
        existingOption.setPrice(new BigDecimal("3000"));
        existing.setExpressPricingOptions(new ArrayList<>(List.of(existingOption)));

        LaundryServiceRequest request = createRequest();
        request.setExpressPricingOptions(List.of());

        when(serviceRepository.findById(1L)).thenReturn(Optional.of(existing));
        when(serviceRepository.save(any(LaundryService.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        LaundryServiceResponse response =
                laundryServiceService.updateService(1L, request);

        assertTrue(response.getExpressPricingOptions().isEmpty());
    }


    @Test
    void updateServiceShouldPreserveExistingDescriptionWhenOmitted() {
        LaundryService existing = new LaundryService();
        existing.setId(1L);
        existing.setName("Express Laundry");
        existing.setDescription("Fast laundry service");
        existing.setPrice(new BigDecimal("2500"));
        existing.setCategory("Express");
        existing.setStock(5);
        existing.setAvailable(true);

        LaundryServiceRequest request = createRequest();
        request.setDescription(null);
        request.setExpressPricingOptions(null);

        when(serviceRepository.findById(1L)).thenReturn(Optional.of(existing));
        when(serviceRepository.save(any(LaundryService.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        LaundryServiceResponse response =
                laundryServiceService.updateService(1L, request);

        assertEquals("Fast laundry service", response.getDescription());
    }

}
