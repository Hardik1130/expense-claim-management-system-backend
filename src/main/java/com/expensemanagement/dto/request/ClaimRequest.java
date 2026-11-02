package com.expensemanagement.dto.request;

import jakarta.validation.constraints.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ClaimRequest {
    @NotBlank
    @Size(max = 160)
    private String title;
    @NotNull
    @DecimalMin("0.01")
    @Digits(integer = 10, fraction = 2)
    private BigDecimal amount;
    @NotNull
    private UUID categoryId;
    @NotNull
    @PastOrPresent
    private LocalDate expenseDate;
    @Size(max = 2000)
    private String description;
}
