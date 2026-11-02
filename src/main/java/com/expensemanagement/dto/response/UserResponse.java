package com.expensemanagement.dto.response;

import com.expensemanagement.enums.Role;
import lombok.*;
import java.util.UUID;

@Data @Builder @NoArgsConstructor @AllArgsConstructor
public class UserResponse {
    private UUID id;
    private String name;
    private String email;
    private Role role;
    private boolean active;
}
