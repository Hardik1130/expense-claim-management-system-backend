package com.expensemanagement.controller;

import com.expensemanagement.dto.request.*;
import com.expensemanagement.dto.response.Response;
import com.expensemanagement.dto.response.UserResponse;
import com.expensemanagement.security.CustomUserDetails;
import com.expensemanagement.service.AuthService;
import jakarta.validation.Valid;
import org.springframework.http.*;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/auth")
public class AuthController {
    private final AuthService authService;

    public AuthController(AuthService authService) { this.authService = authService; }

    @PostMapping("/register")
    public ResponseEntity<Response> register(@Valid @RequestBody RegisterRequest request) {
        UserResponse user = authService.register(request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(Response.success("Registration successful.", user));
    }

    @PostMapping("/login")
    public ResponseEntity<Response> login(@Valid @RequestBody LoginRequest request) {
        return ResponseEntity.ok(Response.success("Login successful.", authService.login(request)));
    }

    @PostMapping("/refresh")
    public ResponseEntity<Response> refresh(@Valid @RequestBody RefreshTokenRequest request) {
        return ResponseEntity.ok(Response.success("Token refreshed successfully.", authService.refresh(request)));
    }

    @PostMapping("/logout")
    public ResponseEntity<Response> logout(@Valid @RequestBody RefreshTokenRequest request) {
        authService.logout(request);
        return ResponseEntity.ok(Response.success("Logout successful.", null));
    }

    @GetMapping("/me")
    public ResponseEntity<Response> me(@AuthenticationPrincipal CustomUserDetails principal) {
        return ResponseEntity.ok(Response.success("Authenticated user.", java.util.Map.of(
                "id", principal.getId(), "email", principal.getUsername(),
                "role", principal.getRole())));
    }
}
