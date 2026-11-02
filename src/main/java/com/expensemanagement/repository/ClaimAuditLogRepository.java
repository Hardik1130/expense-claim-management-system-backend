package com.expensemanagement.repository;

import com.expensemanagement.entity.ClaimAuditLog;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface ClaimAuditLogRepository extends JpaRepository<ClaimAuditLog, UUID> {
    List<ClaimAuditLog> findAllByClaimIdOrderByCreatedAtDesc(UUID claimId);

    void deleteAllByClaimId(UUID claimId);


}
