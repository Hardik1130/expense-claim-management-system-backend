package com.expensemanagement.service;

import com.expensemanagement.dto.response.DashboardResponse;

import java.util.UUID;

public interface DashboardService {
    DashboardResponse employee(UUID userId);

    DashboardResponse manager();
}
