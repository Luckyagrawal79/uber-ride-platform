package com.uber.driverservice.controller;

import com.uber.common.dto.request.*;
import com.uber.common.dto.response.*;
import com.uber.driverservice.service.DriverService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import java.util.Map;

@RestController @RequestMapping("/api/drivers") @RequiredArgsConstructor
public class DriverController {
    private final DriverService service;

    @PostMapping("/{userId}")
    public ResponseEntity<DriverResponse> create(@PathVariable Long userId, @Valid @RequestBody CreateDriverRequest req) {
        return ResponseEntity.status(HttpStatus.CREATED).body(service.createDriver(userId, req));
    }

    @GetMapping("/{id}")
    public ResponseEntity<DriverResponse> getById(@PathVariable Long id) { 
        return ResponseEntity.ok(service.getById(id)); 
    }

    @GetMapping
    public ResponseEntity<PaginatedResponse<DriverResponse>> getAll(Pageable pageable) { 
        return ResponseEntity.ok(service.getAll(pageable)); 
    }

    @PutMapping("/{id}")
    public ResponseEntity<DriverResponse> update(@PathVariable Long id, @Valid @RequestBody UserUpdateRequest req) {
        return ResponseEntity.ok(service.update(id, req));
    }

    @PutMapping("/{id}/status")
    public ResponseEntity<DriverResponse> toggleStatus(@PathVariable Long id, @RequestBody Map<String, Boolean> body) {
        return ResponseEntity.ok(service.toggleOnline(id, body.getOrDefault("online", false)));
    }

    @PutMapping("/{id}/location")
    public ResponseEntity<Void> updateLocation(@PathVariable Long id, @RequestBody LocationDto loc) {
        service.updateLocation(id, loc.getLatitude(), loc.getLongitude());
        return ResponseEntity.noContent().build();
    }
}
