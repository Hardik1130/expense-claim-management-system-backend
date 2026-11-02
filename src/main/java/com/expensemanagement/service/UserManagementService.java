package com.expensemanagement.service;

import com.expensemanagement.dto.response.UserSummaryResponse;
import com.expensemanagement.dto.request.UserStatusRequest;
import org.springframework.data.domain.Page;

import java.util.UUID;

public interface UserManagementService {
    Page<UserSummaryResponse> list(int page, int size);

    UserSummaryResponse get(UUID id);

    UserSummaryResponse updateStatus(UUID id, UserStatusRequest request);
}