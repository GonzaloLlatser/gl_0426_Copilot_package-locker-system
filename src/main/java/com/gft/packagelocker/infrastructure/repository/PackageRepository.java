package com.gft.packagelocker.infrastructure.repository;

import com.gft.packagelocker.domain.model.Package;
import com.gft.packagelocker.domain.model.PackageStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface PackageRepository extends JpaRepository<Package, UUID> {

    Optional<Package> findByIdAndStatus(UUID id, PackageStatus status);

    List<Package> findByCustomerId(UUID customerId);

    boolean existsByAssignedLockerIdAndStatusNot(UUID lockerId, PackageStatus status);
}
