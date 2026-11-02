package com.expensemanagement.dto.request;

import lombok.*;
import jakarta.validation.constraints.NotNull;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserStatusRequest {
    @NotNull
    private Boolean active;
}
