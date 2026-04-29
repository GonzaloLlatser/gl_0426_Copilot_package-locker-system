package com.gft.packagelocker.application.delivery;

import com.gft.packagelocker.domain.model.LockerSize;
import jakarta.validation.constraints.NotNull;

import java.util.UUID;

public class CreateDeliveryRequest {

    @NotNull
    private UUID customerId;

    @NotNull
    private LockerSize packageSize;

    public CreateDeliveryRequest() {
    }

    public CreateDeliveryRequest(UUID customerId, LockerSize packageSize) {
        this.customerId = customerId;
        this.packageSize = packageSize;
    }

    public UUID getCustomerId() {
        return customerId;
    }

    public LockerSize getPackageSize() {
        return packageSize;
    }
}
