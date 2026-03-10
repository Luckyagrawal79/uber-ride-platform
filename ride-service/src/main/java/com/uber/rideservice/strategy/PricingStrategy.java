package com.uber.rideservice.strategy;

import com.uber.common.enums.VehicleType;

/**
 * STRATEGY PATTERN: Different pricing algorithms.
 * Easy to add surge pricing, promo discounts, etc.
 */
public interface PricingStrategy {
    double calculatePrice(double distanceKm, VehicleType vehicleType);
}
