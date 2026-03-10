package com.uber.paymentservice.controller;

import com.uber.common.dto.response.PaymentResponse;
import com.uber.paymentservice.service.PaymentService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController @RequestMapping("/api/payments") @RequiredArgsConstructor
public class PaymentController {
    
    private final PaymentService service;

    @GetMapping("/ride/{rideId}")
    public ResponseEntity<PaymentResponse> getByRide(@PathVariable Long rideId) {
        return ResponseEntity.ok(service.getByRide(rideId));
    }

    @GetMapping("/passenger/{passengerId}")
    public ResponseEntity<List<PaymentResponse>> getByPassenger(@PathVariable Long passengerId) {
        return ResponseEntity.ok(service.getByPassenger(passengerId));
    }
}
