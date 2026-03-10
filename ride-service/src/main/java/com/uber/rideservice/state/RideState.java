package com.uber.rideservice.state;

import com.uber.rideservice.model.Ride;

/**
 * STATE PATTERN: Each ride status is a state with defined valid transitions.
 * Prevents invalid state changes (e.g., can't finish a pending ride).
 */
public interface RideState {

    default void accept(Ride ride) { 
        throw new IllegalStateException("Cannot accept ride in " + ride.getStatus() + " state"); 
    }

    default void reject(Ride ride, String reason) { 
        throw new IllegalStateException("Cannot reject ride in " + ride.getStatus() + " state"); 
    }

    default void start(Ride ride) {
         throw new IllegalStateException("Cannot start ride in " + ride.getStatus() + " state");
    }

    default void finish(Ride ride) {
         throw new IllegalStateException("Cannot finish ride in " + ride.getStatus() + " state");
    }

    default void cancel(Ride ride) { 
        throw new IllegalStateException("Cannot cancel ride in " + ride.getStatus() + " state"); 
    }
    
    default void panic(Ride ride) { 
        throw new IllegalStateException("Cannot trigger panic in " + ride.getStatus() + " state"); 
    }
}
