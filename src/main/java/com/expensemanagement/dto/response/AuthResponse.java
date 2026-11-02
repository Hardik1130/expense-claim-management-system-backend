package com.expensemanagement.dto.response;

import com.expensemanagement.enums.Role;
import lombok.*;

import java.time.LocalDateTime;
import java.util.UUID;

@Data @Builder @NoArgsConstructor @AllArgsConstructor
public class AuthResponse {
    private String accessToken;
    private String refreshToken;
    private String tokenType;
    private long accessTokenExpiresIn;
    private long refreshTokenExpiresIn;
    private UUID userId;
    private String name;
    private String email;
    private Role role;
    private LocalDateTime issuedAt;
}
