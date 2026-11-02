package com.expensemanagement.dto.response;

import java.time.LocalDateTime;
import java.util.UUID;

import lombok.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ReceiptResponse {
    private UUID id;
    private UUID claimId;
    private String originalFilename;
    private String contentType;
    private long fileSize;
    private LocalDateTime uploadedAt;
}
