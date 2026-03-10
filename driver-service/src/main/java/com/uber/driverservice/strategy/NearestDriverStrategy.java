package com.uber.driverservice.strategy;

import com.uber.common.event.RideRequestedEvent;
import com.uber.driverservice.model.Driver;
import org.springframework.stereotype.Component;
import java.util.*;

/**
 * Matches the nearest available driver to the ride pickup location.
 * Uses Haversine formula for distance calculation.
 */
@Component("nearestDriver")
public class NearestDriverStrategy implements DriverMatchingStrategy {

    @Override
    public Optional<Driver> findBestDriver(List<Driver> drivers, RideRequestedEvent event) {
        return drivers.stream()
                .filter(d -> d.getCurrentLatitude() != null && d.getCurrentLongitude() != null)
                .min(Comparator.comparingDouble(d ->
                        haversine(d.getCurrentLatitude(), d.getCurrentLongitude(),
                                event.getDepartureLatitude(), event.getDepartureLongitude())));
    }

    private double haversine(double lat1, double lon1, double lat2, double lon2) {
        final double R = 6371;
        double dLat = Math.toRadians(lat2 - lat1), dLon = Math.toRadians(lon2 - lon1);
        double a = Math.sin(dLat / 2) * Math.sin(dLat / 2) +
                Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2)) *
                Math.sin(dLon / 2) * Math.sin(dLon / 2);
        return R * 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
    }
}
