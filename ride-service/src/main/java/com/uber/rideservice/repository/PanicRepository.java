package com.uber.rideservice.repository;

import com.uber.rideservice.model.Panic;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface PanicRepository extends JpaRepository<Panic, Long> {
    Page<Panic> findAll(Pageable pageable);
    List<Panic> findByResolvedFalse();
}
