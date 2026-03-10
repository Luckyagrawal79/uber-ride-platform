package com.uber.paymentservice.strategy;

/**
 * STRATEGY PATTERN: Each payment method has its own processing logic.
 */
public interface PaymentProcessor {
    PaymentResult process(Long rideId, Long passengerId, double amount);
}
