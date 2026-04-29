package com.gft.packagelocker.api.dto;

import com.gft.packagelocker.domain.model.LockerSize;
import com.gft.packagelocker.domain.model.LockerStatus;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

@Schema(description = "Request to register a new locker")
public class CreateLockerRequestDto {

    @NotBlank(message = "code is required")
    @Schema(description = "Unique locker code", example = "L-01")
    private String code;

    @NotNull(message = "size is required")
    @Schema(description = "Physical size of the locker: S, M or L", example = "L")
    private LockerSize size;

    @NotNull(message = "status is required")
    @Schema(description = "Initial status of the locker", example = "AVAILABLE")
    private LockerStatus status;

    public String getCode() { return code; }
    public void setCode(String code) { this.code = code; }

    public LockerSize getSize() { return size; }
    public void setSize(LockerSize size) { this.size = size; }

    public LockerStatus getStatus() { return status; }
    public void setStatus(LockerStatus status) { this.status = status; }
}
