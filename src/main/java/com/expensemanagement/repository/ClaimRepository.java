package com.expensemanagement.repository;

import com.expensemanagement.entity.Claim;
import com.expensemanagement.enums.ClaimStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.*;

public interface ClaimRepository extends JpaRepository<Claim, UUID> {
    Page<Claim> findByUserId(UUID userId, Pageable pageable);

    Page<Claim> findByUserIdAndStatus(UUID userId, ClaimStatus status, Pageable pageable);

    @Query(
            value = """
        SELECT c FROM Claim c
        JOIN FETCH c.user
        JOIN FETCH c.category
        WHERE
            (:status IS NULL OR c.status = :status)
            AND (:userId IS NULL OR c.user.id = :userId)
            AND (:fromDate IS NULL OR c.expenseDate >= :fromDate)
            AND (:toDate IS NULL OR c.expenseDate <= :toDate)
            AND (
                CAST(:q AS string) IS NULL
                OR LOWER(c.title) LIKE CONCAT('%', LOWER(CAST(:q AS string)), '%')
                OR LOWER(c.description) LIKE CONCAT('%', LOWER(CAST(:q AS string)), '%')
            )
        """,
            countQuery = """
        SELECT COUNT(c) FROM Claim c
        WHERE
            (:status IS NULL OR c.status = :status)
            AND (:userId IS NULL OR c.user.id = :userId)
            AND (:fromDate IS NULL OR c.expenseDate >= :fromDate)
            AND (:toDate IS NULL OR c.expenseDate <= :toDate)
            AND (
                CAST(:q AS string) IS NULL
                OR LOWER(c.title) LIKE CONCAT('%', LOWER(CAST(:q AS string)), '%')
                OR LOWER(c.description) LIKE CONCAT('%', LOWER(CAST(:q AS string)), '%')
            )
        """
    )
    Page<Claim> searchManager(
            @Param("status") ClaimStatus status,
            @Param("userId") UUID userId,
            @Param("fromDate") LocalDate fromDate,
            @Param("toDate") LocalDate toDate,
            @Param("q") String q,
            Pageable pageable
    );

    @Query("""
    SELECT c FROM Claim c
    WHERE c.user.id = :uid
      AND (:status IS NULL OR c.status = :status)
      AND (
          :q IS NULL
          OR LOWER(c.title) LIKE CONCAT('%', LOWER(CAST(:q AS string)), '%')
          OR LOWER(c.description) LIKE CONCAT('%', LOWER(CAST(:q AS string)), '%')
      )
    """)
    Page<Claim> searchEmployee(
            @Param("uid") UUID uid,
            @Param("status") ClaimStatus status,
            @Param("q") String q,
            Pageable pageable
    );

    long countByUserId(UUID userId);

    long countByStatus(ClaimStatus status);

    long countByUserIdAndStatus(UUID userId, ClaimStatus status);

    List<Claim> findAllByUserId(UUID userId);

    List<Claim> findAllByStatus(ClaimStatus status);
}
