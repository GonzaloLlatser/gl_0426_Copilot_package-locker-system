package com.gft.packagelocker.api.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;

@Schema(description = "Request to pick up a package using a PIN")
public class PickupRequestDto {

    @NotBlank(message = "pin is required")
    @Schema(description = "6-digit pickup PIN", example = "123456")
    private String pin;

    public String getPin() { return pin; }
    public void setPin(String pin) { this.pin = pin; }
}
