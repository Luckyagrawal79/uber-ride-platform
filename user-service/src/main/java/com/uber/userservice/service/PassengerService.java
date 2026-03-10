package com.uber.userservice.service;

import com.uber.common.dto.request.*;
import com.uber.common.dto.response.*;
import com.uber.userservice.model.*;
import com.uber.userservice.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;
import java.util.List;

@Service @RequiredArgsConstructor
public class PassengerService {
    
    private final PassengerRepository passengerRepo;
    private final FavoriteRouteRepository favRouteRepo;

    @Transactional
    public UserResponse createPassenger(Long userId, RegisterRequest req) {
        Passenger p = Passenger.builder().id(userId).email(req.getEmail())
                .name(req.getName()).surname(req.getSurname())
                .telephoneNumber(req.getTelephoneNumber()).address(req.getAddress())
                .profilePicture(req.getProfilePicture()).active(true).build();
        return toResponse(passengerRepo.save(p));
    }

    public UserResponse getById(Long id) {
        return toResponse(findById(id));
    }

    public UserResponse getByEmail(String email) {
        return toResponse(passengerRepo.findByEmail(email)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND)));
    }

    public PaginatedResponse<UserResponse> getAll(Pageable pageable) {
        Page<Passenger> page = passengerRepo.findAll(pageable);
        return new PaginatedResponse<>((int) page.getTotalElements(),
                page.getContent().stream().map(this::toResponse).toList());
    }

    @Transactional
    public UserResponse update(Long id, UserUpdateRequest req) {
        Passenger p = findById(id);
        if (req.getName() != null) p.setName(req.getName());
        if (req.getSurname() != null) p.setSurname(req.getSurname());
        if (req.getTelephoneNumber() != null) p.setTelephoneNumber(req.getTelephoneNumber());
        if (req.getAddress() != null) p.setAddress(req.getAddress());
        if (req.getProfilePicture() != null) p.setProfilePicture(req.getProfilePicture());
        return toResponse(passengerRepo.save(p));
    }

    @Transactional
    public void blockUser(Long id, boolean block) {
        Passenger p = findById(id);
        p.setBlocked(block);
        passengerRepo.save(p);
    }

    // Favorite Routes
    @Transactional
    public FavoriteRouteResponse addFavoriteRoute(Long passengerId, FavoriteRouteRequest req) {
        FavoriteRoute fr = FavoriteRoute.builder().name(req.getName()).passengerId(passengerId)
                .departureLatitude(req.getDeparture().getLatitude()).departureLongitude(req.getDeparture().getLongitude())
                .departureAddress(req.getDeparture().getAddress())
                .destinationLatitude(req.getDestination().getLatitude()).destinationLongitude(req.getDestination().getLongitude())
                .destinationAddress(req.getDestination().getAddress()).build();
        FavoriteRoute saved = favRouteRepo.save(fr);
        return toFavResponse(saved);
    }

    public List<FavoriteRouteResponse> getFavoriteRoutes(Long passengerId) {
        return favRouteRepo.findByPassengerId(passengerId).stream().map(this::toFavResponse).toList();
    }

    @Transactional
    public void deleteFavoriteRoute(Long id) { 
        favRouteRepo.deleteById(id); 
    }

    private Passenger findById(Long id) {
        return passengerRepo.findById(id).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Passenger not found"));
    }

    private UserResponse toResponse(Passenger p) {
        return UserResponse.builder().id(p.getId()).email(p.getEmail()).name(p.getName()).surname(p.getSurname())
                .profilePicture(p.getProfilePicture()).telephoneNumber(p.getTelephoneNumber())
                .address(p.getAddress()).blocked(p.isBlocked()).active(p.isActive()).build();
    }

    private FavoriteRouteResponse toFavResponse(FavoriteRoute fr) {
        return FavoriteRouteResponse.builder().id(fr.getId()).name(fr.getName())
                .departureLatitude(fr.getDepartureLatitude()).departureLongitude(fr.getDepartureLongitude()).departureAddress(fr.getDepartureAddress())
                .destinationLatitude(fr.getDestinationLatitude()).destinationLongitude(fr.getDestinationLongitude()).destinationAddress(fr.getDestinationAddress()).build();
    }
}
