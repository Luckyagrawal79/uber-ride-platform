package com.uber.rideservice.state;

import com.uber.common.enums.RideStatus;

/**
 * Factory for creating the correct RideState based on current status.
 */
public final class RideStateFactory {
    private RideStateFactory() {}

    public static RideState getState(RideStatus status) {
        return switch (status) {
            case PENDING, SCHEDULED -> new PendingState();
            case ACCEPTED -> new AcceptedState();
            case STARTED, PANIC -> new StartedState();
            case FINISHED, CANCELED, REJECTED -> throw new IllegalStateException("Ride in terminal state: " + status);
        };
    }
}
