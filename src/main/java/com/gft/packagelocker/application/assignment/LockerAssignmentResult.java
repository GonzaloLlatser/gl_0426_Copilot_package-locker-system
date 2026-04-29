package com.gft.packagelocker.application.assignment;

import java.util.UUID;

public class LockerAssignmentResult {

    private final boolean success;
    private final UUID lockerId;
    private final String reason;

    private LockerAssignmentResult(boolean success, UUID lockerId, String reason) {
        this.success = success;
        this.lockerId = lockerId;
        this.reason = reason;
    }

    public static LockerAssignmentResult success(UUID lockerId) {
        return new LockerAssignmentResult(true, lockerId, null);
    }

    public static LockerAssignmentResult failure(String reason) {
        return new LockerAssignmentResult(false, null, reason);
    }

    public boolean isSuccess() {
        return success;
    }

    public UUID getLockerId() {
        return lockerId;
    }

    public String getReason() {
        return reason;
    }
}
