package com.uber.driverservice.repository;

import com.uber.common.enums.*;
import com.uber.driverservice.model.Driver;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.*;

public interface DriverRepository extends JpaRepository<Driver, Long> {
    Optional<Driver> findByEmail(String email);
    List<Driver> findByStatusAndVehicleType(DriverStatus status, VehicleType type);
    List<Driver> findByStatus(DriverStatus status);
    Page<Driver> findAll(Pageable pageable);
}
