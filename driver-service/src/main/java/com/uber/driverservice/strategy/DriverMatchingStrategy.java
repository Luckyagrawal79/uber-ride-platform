package com.uber.driverservice.strategy;

import com.uber.common.event.RideRequestedEvent;
import com.uber.driverservice.model.Driver;
import java.util.List;
import java.util.Optional;

/**
 * Strategy Pattern: Different algorithms for matching drivers to rides.
 * Allows swapping matching logic without changing the service code.
 */
public interface DriverMatchingStrategy {
    Optional<Driver> findBestDriver(List<Driver> availableDrivers, RideRequestedEvent event);
}
