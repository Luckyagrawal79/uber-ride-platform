package com.uber.driverservice.repository;

import com.uber.driverservice.model.WorkHours;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import java.time.LocalDateTime;
import java.util.*;

public interface WorkHoursRepository extends JpaRepository<WorkHours, Long> {
    
    @Query("SELECT w FROM WorkHours w WHERE w.driverId = :driverId AND w.endTime IS NULL")
    Optional<WorkHours> findActiveByDriverId(Long driverId);

    @Query("SELECT w FROM WorkHours w WHERE w.driverId = :driverId AND w.startTime >= :startOfDay")
    List<WorkHours> findTodayByDriverId(Long driverId, LocalDateTime startOfDay);
}
