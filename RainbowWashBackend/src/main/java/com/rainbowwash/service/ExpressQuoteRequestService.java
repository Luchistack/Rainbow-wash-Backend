package com.rainbowwash.service;

import com.rainbowwash.model.ExpressQuoteRequest;
import com.rainbowwash.repository.ExpressQuoteRequestRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

@Service
public class ExpressQuoteRequestService {
    private final ExpressQuoteRequestRepository repository;

    public ExpressQuoteRequestService(ExpressQuoteRequestRepository repository) {
        this.repository = repository;
    }

    @Transactional
    public ExpressQuoteRequest create(ExpressQuoteRequest request) {
        request.setId(null);
        request.setReferenceId("EXQ-" + UUID.randomUUID().toString()
                .substring(0, 8).toUpperCase());
        request.setStatus("Pending Quote");
        request.setQuotedPrice(null);
        request.setArchived(false);
        request.setPrinted(false);
        return repository.save(request);
    }

    public List<ExpressQuoteRequest> findAll() {
        return repository.findAll();
    }

    @Transactional
    public ExpressQuoteRequest update(Long id, BigDecimal quotedPrice,
                                     String status, Boolean archived,
                                     Boolean printed) {
        ExpressQuoteRequest request = repository.findById(id)
                .orElseThrow(() ->
                        new IllegalArgumentException("Express request not found"));

        if (quotedPrice != null) {
            if (quotedPrice.signum() <= 0) {
                throw new IllegalArgumentException(
                        "Quote price must be greater than zero");
            }
            request.setQuotedPrice(quotedPrice);
        }

        if (status != null) {
            if (!List.of("Pending Quote", "Quoted", "Confirmed",
                    "In Progress", "Completed", "Cancelled").contains(status)) {
                throw new IllegalArgumentException("Invalid express request status");
            }
            request.setStatus(status);
        }

        if (archived != null) request.setArchived(archived);
        if (printed != null) request.setPrinted(printed);

        return repository.save(request);
    }
}
