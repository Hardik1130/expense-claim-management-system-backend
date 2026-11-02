package com.expensemanagement.dto.response;

import com.expensemanagement.enums.AuditAction;

import java.time.LocalDateTime;
import java.util.UUID;

import lombok.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AuditLogResponse {
    private UUID id;
    private String performedBy;
    private AuditAction action;
    private String oldValue;
    private String newValue;
    private String comment;
    private LocalDateTime createdAt;
}
