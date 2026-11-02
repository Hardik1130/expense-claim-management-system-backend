package com.expensemanagement.service;

import com.expensemanagement.dto.request.ReviewClaimRequest;
import com.expensemanagement.dto.response.*;
import com.expensemanagement.enums.ClaimStatus;

import java.time.LocalDate;
import java.util.*;

public interface ManagerClaimService {
    ClaimResponse get(UUID claimId);

    java.util.List<com.expensemanagement.dto.response.AuditLogResponse> timeline(UUID claimId);

    PageResponse<ClaimResponse> list(ClaimStatus status, UUID employeeId, LocalDate fromDate, LocalDate toDate, String search, int page, int size);

    ClaimResponse approve(UUID managerId, UUID claimId, ReviewClaimRequest request);

    ClaimResponse reject(UUID managerId, UUID claimId, ReviewClaimRequest request);
}