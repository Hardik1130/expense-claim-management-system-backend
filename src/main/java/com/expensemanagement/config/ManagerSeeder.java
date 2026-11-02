package com.expensemanagement.config;

import com.expensemanagement.entity.User;
import com.expensemanagement.enums.Role;
import com.expensemanagement.repository.UserRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
public class ManagerSeeder {
    @Bean
    CommandLineRunner seedManager(UserRepository userRepository,
                                  PasswordEncoder passwordEncoder,
                                  @Value("${app.bootstrap.manager.name}") String name,
                                  @Value("${app.bootstrap.manager.email}") String email,
                                  @Value("${app.bootstrap.manager.password}") String password) {
        return args -> {
            if (password == null || password.isBlank()) {
                throw new IllegalStateException("MANAGER_PASSWORD must be configured before startup.");
            }
            String normalizedEmail = email.trim().toLowerCase();
            userRepository.findByEmailIgnoreCase(normalizedEmail).ifPresentOrElse(
                    existing -> {
                        if (existing.getRole() != Role.MANAGER) {
                            throw new IllegalStateException("Configured manager email belongs to a non-manager account.");
                        }
                    },
                    () -> userRepository.save(User.builder()
                            .name(name).email(normalizedEmail)
                            .password(passwordEncoder.encode(password))
                            .role(Role.MANAGER).active(true).build())
            );
        };
    }
}
