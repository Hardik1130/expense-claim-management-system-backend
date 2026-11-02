package com.expensemanagement.dto.request;

import jakarta.validation.constraints.*;
import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ReviewClaimRequest {
    @Size(max = 2000)
    private String comment;
}
