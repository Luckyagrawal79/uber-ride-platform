package com.uber.rideservice.state;

import com.uber.common.enums.RideStatus;
import com.uber.rideservice.model.Ride;
import java.time.LocalDateTime;

public class AcceptedState implements RideState {

    @Override 
    public void start(Ride ride) { 
        ride.setStatus(RideStatus.STARTED); 
        ride.setStartTime(LocalDateTime.now()); 
    }

    @Override 
    public void cancel(Ride ride) {
         ride.setStatus(RideStatus.CANCELED);
     }
}
