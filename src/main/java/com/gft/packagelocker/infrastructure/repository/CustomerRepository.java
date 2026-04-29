package com.gft.packagelocker.infrastructure.repository;

import com.gft.packagelocker.domain.model.Customer;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface CustomerRepository extends JpaRepository<Customer, UUID> {

    Optional<Customer> findByName(String name);

    boolean existsByName(String name);
}
