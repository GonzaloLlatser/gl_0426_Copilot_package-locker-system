package com.gft.packagelocker.infrastructure.repository;

import com.gft.packagelocker.domain.model.PickupCode;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface PickupCodeRepository extends JpaRepository<PickupCode, UUID> {

    Optional<PickupCode> findByDeliveryIdAndPinAndActiveTrue(UUID deliveryId, String pin);

    Optional<PickupCode> findByDeliveryId(UUID deliveryId);
}
