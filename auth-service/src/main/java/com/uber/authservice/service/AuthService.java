package com.uber.authservice.service;

import com.uber.authservice.model.AuthUser;
import com.uber.authservice.repository.AuthUserRepository;
import com.uber.authservice.security.JwtTokenProvider;
import com.uber.common.dto.request.LoginRequest;
import com.uber.common.dto.request.RegisterRequest;
import com.uber.common.dto.response.AuthResponse;
import com.uber.common.dto.response.UserResponse;
import com.uber.common.enums.Role;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.*;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

@Service
@RequiredArgsConstructor
public class AuthService {
    private final AuthUserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenProvider tokenProvider;
    private final AuthenticationManager authManager;

    @Transactional
    public AuthResponse register(RegisterRequest request) {

        if (userRepository.existsByEmail(request.getEmail()))
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Email already registered");

        AuthUser user = AuthUser.builder().email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .name(request.getName()).surname(request.getSurname())
                .role(Role.PASSENGER).active(true).blocked(false).build();

        return generateAuthResponse(userRepository.save(user));
    }


    @Transactional
    public AuthResponse registerDriver(RegisterRequest request) {

        if (userRepository.existsByEmail(request.getEmail()))
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Email already registered");

        AuthUser user = AuthUser.builder().email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .name(request.getName()).surname(request.getSurname())
                .role(Role.DRIVER).active(true).blocked(false).build();

        return generateAuthResponse(userRepository.save(user));
    }



    public AuthResponse login(LoginRequest request) {

        authManager.authenticate(new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword()));

        AuthUser user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED));

        if (user.isBlocked()) throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Account is blocked");

        return generateAuthResponse(user);
    }



    public AuthResponse refreshToken(String refreshToken) {

        if (!tokenProvider.validateToken(refreshToken))
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid refresh token");

        AuthUser user = userRepository.findByEmail(tokenProvider.getEmailFromToken(refreshToken))
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED));

        return generateAuthResponse(user);
    }


    public UserResponse validateToken(String token) {

        if (!tokenProvider.validateToken(token))
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED);

        AuthUser user = userRepository.findByEmail(tokenProvider.getEmailFromToken(token))
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED));
                
        return toUserResponse(user);
    }


    private AuthResponse generateAuthResponse(AuthUser user) {
        return AuthResponse.builder()
                .accessToken(tokenProvider.generateAccessToken(user.getId(), user.getEmail(), user.getRole().name()))
                .refreshToken(tokenProvider.generateRefreshToken(user.getEmail()))
                .user(toUserResponse(user)).build();
    }


    private UserResponse toUserResponse(AuthUser u) {
        return UserResponse.builder().id(u.getId()).email(u.getEmail()).name(u.getName())
                .surname(u.getSurname()).role(u.getRole()).blocked(u.isBlocked()).active(u.isActive()).build();
    }

}
