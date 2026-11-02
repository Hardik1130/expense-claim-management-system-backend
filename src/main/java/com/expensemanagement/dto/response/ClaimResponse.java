package com.expensemanagement.dto.response;

import com.expensemanagement.enums.ClaimStatus;

import java.math.BigDecimal;
import java.time.*;
import java.util.UUID;

import lombok.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ClaimResponse {
    private UUID id;
    private UUID employeeId;
    private String employeeName;
    private String employeeEmail;
    private String title;
    private BigDecimal amount;
    private UUID categoryId;
    private String categoryName;
    private LocalDate expenseDate;
    private String description;
    private ClaimStatus status;
    private String managerComment;
    private UUID reviewedBy;
    private LocalDateTime reviewedAt;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private boolean receiptAttached;
    private UUID receiptId;
}
