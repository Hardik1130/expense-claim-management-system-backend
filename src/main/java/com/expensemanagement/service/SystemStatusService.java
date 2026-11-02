package com.expensemanagement.service;

import com.expensemanagement.dto.response.SystemStatusResponse;
import org.springframework.core.env.Environment;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class SystemStatusService {
    private final JdbcTemplate jdbcTemplate;
    private final Environment environment;

    public SystemStatusService(JdbcTemplate jdbcTemplate, Environment environment) {
        this.jdbcTemplate = jdbcTemplate;
        this.environment = environment;
    }

    public SystemStatusResponse getStatus() {
        boolean connected = false;
        try {
            Integer result = jdbcTemplate.queryForObject("SELECT 1", Integer.class);
            connected = result != null && result == 1;
        } catch (Exception ignored) {
            // Return status only; do not expose database credentials or internal exception details.
        }
        return SystemStatusResponse.builder()
                .application(environment.getProperty("spring.application.name", "expense-claim-management"))
                .applicationStatus("UP")
                .database("PostgreSQL")
                .databaseStatus(connected ? "UP" : "DOWN")
                .javaVersion(System.getProperty("java.version"))
                .checkedAt(LocalDateTime.now())
                .build();
    }
}
