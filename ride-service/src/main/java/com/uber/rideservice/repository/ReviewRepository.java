package com.uber.rideservice.repository;

import com.uber.rideservice.model.Review;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import java.util.List;

public interface ReviewRepository extends JpaRepository<Review, Long> {
    
    List<Review> findByRideId(Long rideId);
    List<Review> findByDriverId(Long driverId);

    @Query("SELECT AVG(r.driverRating) FROM Review r WHERE r.driverId = :driverId")
    Double findAverageDriverRating(Long driverId);
}
