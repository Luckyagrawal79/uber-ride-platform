package com.uber.driverservice.strategy;

import com.uber.common.event.RideRequestedEvent;
import com.uber.driverservice.model.Driver;
import org.springframework.stereotype.Component;
import java.util.*;

/**
 * Matches the first available driver (FIFO). Simple fallback strategy.
 */
@Component("highestRated")
public class HighestRatedDriverStrategy implements DriverMatchingStrategy {

    @Override
    public Optional<Driver> findBestDriver(List<Driver> drivers, RideRequestedEvent event) {
        // In production, would fetch ratings from ride-service. Here uses FIFO as fallback.
        return drivers.stream().findFirst();
    }
}
