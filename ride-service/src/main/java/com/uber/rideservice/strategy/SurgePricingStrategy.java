package com.uber.rideservice.strategy;

import com.uber.common.enums.VehicleType;
import org.springframework.stereotype.Component;

/**
 * Surge pricing: multiplier applied during peak hours.
 */
@Component("surgePricing")
public class SurgePricingStrategy implements PricingStrategy {
    private static final double SURGE_MULTIPLIER = 1.5;
    private final StandardPricingStrategy standardPricing = new StandardPricingStrategy();

    @Override
    public double calculatePrice(double distanceKm, VehicleType type) {
        return Math.round(standardPricing.calculatePrice(distanceKm, type) * SURGE_MULTIPLIER * 100.0) / 100.0;
    }
}
