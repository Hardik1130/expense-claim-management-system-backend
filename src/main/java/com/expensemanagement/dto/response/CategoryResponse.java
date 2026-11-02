package com.expensemanagement.dto.response;

import java.time.LocalDateTime;
import java.util.UUID;

import lombok.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CategoryResponse {
    private UUID id;
    private String name;
    private boolean active;
    private LocalDateTime createdAt;
}
