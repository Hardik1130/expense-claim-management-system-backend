package com.expensemanagement.service;

import com.expensemanagement.dto.request.LoginRequest;
import com.expensemanagement.dto.request.RefreshTokenRequest;
import com.expensemanagement.dto.request.RegisterRequest;
import com.expensemanagement.dto.response.AuthResponse;
import com.expensemanagement.dto.response.UserResponse;

public interface AuthService {
    UserResponse register(RegisterRequest request);

    AuthResponse login(LoginRequest request);

    AuthResponse refresh(RefreshTokenRequest request);

    void logout(RefreshTokenRequest request);
}
