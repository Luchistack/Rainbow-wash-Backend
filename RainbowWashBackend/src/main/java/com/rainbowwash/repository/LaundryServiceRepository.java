package com.rainbowwash.repository;

import com.rainbowwash.model.LaundryService;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface LaundryServiceRepository extends JpaRepository<LaundryService, Long> {
    boolean existsByName(String name);
    List<LaundryService> findByAvailableTrue();
}