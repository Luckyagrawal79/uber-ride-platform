package com.uber.rideservice.state;

import com.uber.common.enums.RideStatus;
import com.uber.rideservice.model.Ride;

public class PendingState implements RideState {

    @Override 
    public void accept(Ride ride) { 
        ride.setStatus(RideStatus.ACCEPTED); 
    }

    @Override 
    public void reject(Ride ride, String reason) { 
        ride.setStatus(RideStatus.REJECTED); 
        ride.setRejectionReason(reason); 
    }

    @Override 
    public void cancel(Ride ride) {
         ride.setStatus(RideStatus.CANCELED); 
    }
}
