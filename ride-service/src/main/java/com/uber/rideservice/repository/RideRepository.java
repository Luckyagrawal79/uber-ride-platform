package com.uber.rideservice.repository;

import com.uber.common.enums.RideStatus;
import com.uber.rideservice.model.Ride;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import java.time.LocalDateTime;
import java.util.List;

public interface RideRepository extends JpaRepository<Ride, Long> {
    
    Page<Ride> findByPassengerId(Long passengerId, Pageable pageable);
    Page<Ride> findByDriverId(Long driverId, Pageable pageable);
    List<Ride> findByPassengerIdAndStatusIn(Long passengerId, List<RideStatus> statuses);
    List<Ride> findByDriverIdAndStatusIn(Long driverId, List<RideStatus> statuses);
    List<Ride> findByStatusAndScheduledTimeBefore(RideStatus status, LocalDateTime time);

    @Query("SELECT r FROM Ride r WHERE r.driverId = :driverId AND r.status = 'FINISHED' AND r.startTime BETWEEN :from AND :to")
    List<Ride> findFinishedByDriverAndDateRange(Long driverId, LocalDateTime from, LocalDateTime to);

    @Query("SELECT r FROM Ride r WHERE r.passengerId = :passengerId AND r.status = 'FINISHED' AND r.startTime BETWEEN :from AND :to")
    List<Ride> findFinishedByPassengerAndDateRange(Long passengerId, LocalDateTime from, LocalDateTime to);
}
