package com.uber.rideservice.strategy;

import com.uber.common.enums.VehicleType;
import org.springframework.stereotype.Component;

@Component("standardPricing")
public class StandardPricingStrategy implements PricingStrategy {
    private static final double BASE_FARE = 50.0;
    private static final double PER_KM_STANDARD = 15.0;
    private static final double PER_KM_LUXURY = 25.0;
    private static final double PER_KM_VAN = 20.0;

    @Override
    public double calculatePrice(double distanceKm, VehicleType type) {
        double perKm = switch (type) {
            case STANDARD -> PER_KM_STANDARD;
            case LUXURY -> PER_KM_LUXURY;
            case VAN -> PER_KM_VAN;
        };
        return Math.round((BASE_FARE + distanceKm * perKm) * 100.0) / 100.0;
    }
}
