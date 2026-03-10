package com.uber.userservice.repository;
import com.uber.userservice.model.FavoriteRoute;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface FavoriteRouteRepository extends JpaRepository<FavoriteRoute, Long> {
    List<FavoriteRoute> findByPassengerId(Long passengerId);
}
