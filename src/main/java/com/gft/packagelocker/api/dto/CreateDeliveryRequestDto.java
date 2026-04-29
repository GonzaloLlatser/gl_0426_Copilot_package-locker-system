package com.gft.packagelocker.api.dto;

import com.gft.packagelocker.domain.model.LockerSize;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;

import java.util.UUID;

@Schema(description = "Request to create a new package delivery")
public class CreateDeliveryRequestDto {

    @NotNull(message = "customerId is required")
    @Schema(description = "ID of the customer receiving the package", example = "3fa85f64-5717-4562-b3fc-2c963f66afa6")
    private UUID customerId;

    @NotNull(message = "packageSize is required")
    @Schema(description = "Size of the package: S, M or L", example = "M")
    private LockerSize packageSize;

    public UUID getCustomerId() { return customerId; }
    public void setCustomerId(UUID customerId) { this.customerId = customerId; }

    public LockerSize getPackageSize() { return packageSize; }
    public void setPackageSize(LockerSize packageSize) { this.packageSize = packageSize; }
}
