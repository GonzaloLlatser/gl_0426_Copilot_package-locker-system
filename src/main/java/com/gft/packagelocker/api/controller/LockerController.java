package com.gft.packagelocker.api.controller;

import com.gft.packagelocker.api.dto.CreateLockerRequestDto;
import com.gft.packagelocker.domain.model.Locker;
import com.gft.packagelocker.infrastructure.repository.LockerRepository;
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

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/lockers")
@Tag(name = "Lockers", description = "Locker management operations")
public class LockerController {

    private final LockerRepository lockerRepository;

    public LockerController(LockerRepository lockerRepository) {
        this.lockerRepository = lockerRepository;
    }

    @GetMapping
    @Operation(summary = "List all lockers")
    public ResponseEntity<List<Locker>> listLockers() {
        return ResponseEntity.ok(lockerRepository.findAll());
    }

    @GetMapping("/{lockerId}")
    @Operation(summary = "Get locker by ID")
    public ResponseEntity<Locker> getLocker(@PathVariable UUID lockerId) {
        return lockerRepository.findById(lockerId)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    @Operation(summary = "Register a new locker")
    public ResponseEntity<Locker> createLocker(@Valid @RequestBody CreateLockerRequestDto dto) {
        Locker locker = new Locker(null, dto.getCode(), dto.getSize(), dto.getStatus());
        Locker saved = lockerRepository.save(locker);
        return ResponseEntity.status(HttpStatus.CREATED).body(saved);
    }
}
