package com.gft.packagelocker.api.controller;

import com.gft.packagelocker.api.dto.CreateDeliveryRequestDto;
import com.gft.packagelocker.api.dto.PickupRequestDto;
import com.gft.packagelocker.application.delivery.CreateDeliveryRequest;
import com.gft.packagelocker.application.delivery.CreateDeliveryResponse;
import com.gft.packagelocker.application.delivery.PackageService;
import com.gft.packagelocker.domain.model.Package;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequestMapping("/api/deliveries")
@Tag(name = "Deliveries", description = "Package delivery lifecycle operations")
public class DeliveryController {

    private final PackageService packageService;

    public DeliveryController(PackageService packageService) {
        this.packageService = packageService;
    }

    @PostMapping
    @Operation(summary = "Create a new delivery and auto-assign a locker")
    public ResponseEntity<CreateDeliveryResponse> createDelivery(
            @Valid @RequestBody CreateDeliveryRequestDto dto) {
        CreateDeliveryRequest request = new CreateDeliveryRequest(dto.getCustomerId(), dto.getPackageSize());
        CreateDeliveryResponse response = packageService.createDelivery(request);
        HttpStatus status = response.isAssigned() ? HttpStatus.CREATED : HttpStatus.UNPROCESSABLE_ENTITY;
        return ResponseEntity.status(status).body(response);
    }

    @PostMapping("/{deliveryId}/deposit")
    @Operation(summary = "Confirm package deposit into assigned locker")
    public ResponseEntity<Package> confirmDeposit(@PathVariable UUID deliveryId) {
        Package pkg = packageService.confirmDeposit(deliveryId);
        return ResponseEntity.ok(pkg);
    }

    @PostMapping("/{deliveryId}/pickup")
    @Operation(summary = "Pick up package using PIN")
    public ResponseEntity<Package> pickup(
            @PathVariable UUID deliveryId,
            @Valid @RequestBody PickupRequestDto dto) {
        Package pkg = packageService.pickup(deliveryId, dto.getPin());
        return ResponseEntity.ok(pkg);
    }

    @GetMapping("/{deliveryId}")
    @Operation(summary = "Get delivery state by ID")
    public ResponseEntity<Package> getDelivery(@PathVariable UUID deliveryId) {
        Package pkg = packageService.getDelivery(deliveryId);
        return ResponseEntity.ok(pkg);
    }
}
