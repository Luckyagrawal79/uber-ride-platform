package com.uber.rideservice.state;

import com.uber.common.enums.RideStatus;
import com.uber.rideservice.model.Ride;
import java.time.LocalDateTime;

public class StartedState implements RideState {

    @Override 
    public void finish(Ride ride) { 
        ride.setStatus(RideStatus.FINISHED); 
        ride.setEndTime(LocalDateTime.now()); 
    }

    @Override public void panic(Ride ride) { 
        ride.setStatus(RideStatus.PANIC); 
        ride.setPanicPressed(true);
    }
}
