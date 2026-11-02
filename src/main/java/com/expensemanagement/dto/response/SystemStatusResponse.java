package com.expensemanagement.dto.response;

import lombok.*;
import java.time.LocalDateTime;

@Data @Builder @NoArgsConstructor @AllArgsConstructor
public class SystemStatusResponse {
    private String application;
    private String applicationStatus;
    private String database;
    private String databaseStatus;
    private String javaVersion;
    private LocalDateTime checkedAt;
}
