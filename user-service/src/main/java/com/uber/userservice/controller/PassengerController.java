package com.uber.userservice.controller;

import com.uber.common.dto.request.*;
import com.uber.common.dto.response.*;
import com.uber.userservice.service.PassengerService;
import jakarta.validation.Valid; import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.http.*; import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController 
@RequestMapping("/api/passengers") 
@RequiredArgsConstructor
public class PassengerController {
    private final PassengerService service;

    @PostMapping("/{userId}")
    public ResponseEntity<UserResponse> create(@PathVariable Long userId, @Valid @RequestBody RegisterRequest req) {
        return ResponseEntity.status(HttpStatus.CREATED).body(service.createPassenger(userId, req));
    }

    @GetMapping("/{id}")
    public ResponseEntity<UserResponse> getById(@PathVariable Long id) { 
        return ResponseEntity.ok(service.getById(id)); 
    }

    @GetMapping("/email/{email}")
    public ResponseEntity<UserResponse> getByEmail(@PathVariable String email) { 
        return ResponseEntity.ok(service.getByEmail(email)); 
    }

    @GetMapping
    public ResponseEntity<PaginatedResponse<UserResponse>> getAll(Pageable pageable) { 
        return ResponseEntity.ok(service.getAll(pageable)); 
    }

    @PutMapping("/{id}")
    public ResponseEntity<UserResponse> update(@PathVariable Long id, @Valid @RequestBody UserUpdateRequest req) {
        return ResponseEntity.ok(service.update(id, req));
    }

    @PutMapping("/{id}/block")
    public ResponseEntity<Void> block(@PathVariable Long id) { 
        service.blockUser(id, true); 
        return ResponseEntity.noContent().build(); 
    }

    @PutMapping("/{id}/unblock")
    public ResponseEntity<Void> unblock(@PathVariable Long id) { 
        service.blockUser(id, false); 
        return ResponseEntity.noContent().build(); 
    }

    @PostMapping("/{passengerId}/favorite-routes")
    public ResponseEntity<FavoriteRouteResponse> addFavRoute(@PathVariable Long passengerId, @Valid @RequestBody FavoriteRouteRequest req) {
        return ResponseEntity.status(HttpStatus.CREATED).body(service.addFavoriteRoute(passengerId, req));
    }

    @GetMapping("/{passengerId}/favorite-routes")
    public ResponseEntity<List<FavoriteRouteResponse>> getFavRoutes(@PathVariable Long passengerId) {
        return ResponseEntity.ok(service.getFavoriteRoutes(passengerId));
    }

    @DeleteMapping("/favorite-routes/{id}")
    public ResponseEntity<Void> deleteFavRoute(@PathVariable Long id) { 
        service.deleteFavoriteRoute(id); 
        return ResponseEntity.noContent().build();
     }
}
