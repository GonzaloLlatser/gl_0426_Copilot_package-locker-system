package com.gft.packagelocker.api.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;

@Schema(description = "Request to register a new customer")
public class CreateCustomerRequestDto {

    @NotBlank(message = "name is required")
    @Schema(description = "Full name of the customer", example = "Jane Doe")
    private String name;

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
}
