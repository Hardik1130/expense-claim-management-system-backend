package com.expensemanagement.repository;

import com.expensemanagement.entity.ClaimReceipt;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface ClaimReceiptRepository
        extends JpaRepository<ClaimReceipt, UUID> {

    Optional<ClaimReceipt> findByClaimId(UUID claimId);

    void deleteByClaimId(UUID claimId);
}
