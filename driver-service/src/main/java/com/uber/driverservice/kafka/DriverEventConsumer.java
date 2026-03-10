package com.uber.driverservice.kafka;

import com.uber.common.event.*;
import com.uber.driverservice.service.DriverService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component @RequiredArgsConstructor @Slf4j
public class DriverEventConsumer {
    private final DriverService driverService;

    @KafkaListener(topics = "ride-requested", groupId = "driver-service-group")
    public void onRideRequested(RideRequestedEvent event) {
        log.info("Received ride-requested event for ride {}", event.getRideId());
        driverService.matchDriverForRide(event);
    }

    @KafkaListener(topics = "ride-status-changed", groupId = "driver-service-group")
    public void onRideStatusChanged(RideStatusChangedEvent event) {
        if (event.getDriverId() != null && (event.getNewStatus().name().equals("FINISHED")
                || event.getNewStatus().name().equals("CANCELLED"))) {
            log.info("Releasing driver {} from ride {}", event.getDriverId(), event.getRideId());
            driverService.releaseDriver(event.getDriverId());
        }
    }
}
