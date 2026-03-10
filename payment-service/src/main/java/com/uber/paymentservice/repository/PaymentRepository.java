package com.uber.paymentservice.repository;
import com.uber.paymentservice.model.Payment;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.*;

public interface PaymentRepository extends JpaRepository<Payment, Long> {
    Optional<Payment> findByRideId(Long rideId);
    List<Payment> findByPassengerId(Long passengerId);
}
