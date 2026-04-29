package com.gft.packagelocker.application.delivery;

import java.util.UUID;

public class CreateDeliveryResponse {

    private final UUID deliveryId;
    private final boolean assigned;
    private final UUID lockerId;
    private final String pin;
    private final String failureReason;

    private CreateDeliveryResponse(UUID deliveryId, boolean assigned, UUID lockerId,
                                   String pin, String failureReason) {
        this.deliveryId = deliveryId;
        this.assigned = assigned;
        this.lockerId = lockerId;
        this.pin = pin;
        this.failureReason = failureReason;
    }

    public static CreateDeliveryResponse success(UUID deliveryId, UUID lockerId, String pin) {
        return new CreateDeliveryResponse(deliveryId, true, lockerId, pin, null);
    }

    public static CreateDeliveryResponse failure(UUID deliveryId, String failureReason) {
        return new CreateDeliveryResponse(deliveryId, false, null, null, failureReason);
    }

    public UUID getDeliveryId() {
        return deliveryId;
    }

    public boolean isAssigned() {
        return assigned;
    }

    public UUID getLockerId() {
        return lockerId;
    }

    public String getPin() {
        return pin;
    }

    public String getFailureReason() {
        return failureReason;
    }
}
