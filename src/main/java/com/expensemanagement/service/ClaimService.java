package com.expensemanagement.service;

import com.expensemanagement.dto.request.ClaimRequest;
import com.expensemanagement.dto.response.*;
import com.expensemanagement.enums.ClaimStatus;

import java.util.*;

public interface ClaimService {
    ClaimResponse create(UUID userId, ClaimRequest request);

    ClaimResponse update(UUID userId, UUID claimId, ClaimRequest request);

    void delete(UUID userId, UUID claimId);

    ClaimResponse get(UUID userId, UUID claimId);

    PageResponse<ClaimResponse> list(UUID userId, ClaimStatus status, String search, int page, int size);

    ClaimResponse submit(UUID userId, UUID claimId);

    List<AuditLogResponse> timeline(UUID userId, UUID claimId);
}