package com.uber.authservice.config;

import com.uber.authservice.model.AuthUser;
import com.uber.authservice.repository.AuthUserRepository;
import com.uber.common.enums.Role;
import lombok.RequiredArgsConstructor; import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component @RequiredArgsConstructor @Slf4j
public class DataSeeder implements CommandLineRunner {

    private final AuthUserRepository repo;
    private final PasswordEncoder encoder;

    @Override public void run(String... args) {

        if (!repo.existsByEmail("admin@uber.com")) {
            
            repo.save(AuthUser.builder()
                .email("admin@uber.com")
                .password(encoder.encode("admin123"))
                .name("Admin")
                .surname("User")
                .role(Role.ADMIN)
                .active(true).build());

            log.info("Admin seeded: admin@uber.com / admin123");
        }
    }
}
