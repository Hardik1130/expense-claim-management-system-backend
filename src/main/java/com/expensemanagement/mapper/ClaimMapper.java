package com.expensemanagement.mapper;

import com.expensemanagement.dto.response.ClaimResponse;
import com.expensemanagement.entity.Claim;
import org.springframework.stereotype.Component;

@Component
public class ClaimMapper {

    public ClaimResponse toResponse(Claim c) {

        return ClaimResponse.builder()
                .id(c.getId())
                .employeeId(c.getUser().getId())
                .employeeName(c.getUser().getName())
                .employeeEmail(c.getUser().getEmail())
                .title(c.getTitle())
                .amount(c.getAmount())
                .categoryId(c.getCategory().getId())
                .categoryName(c.getCategory().getName())
                .expenseDate(c.getExpenseDate())
                .description(c.getDescription())
                .status(c.getStatus())
                .managerComment(c.getManagerComment())
                .reviewedBy(c.getReviewedBy() == null
                        ? null : c.getReviewedBy().getId())
                .reviewedAt(c.getReviewedAt())
                .createdAt(c.getCreatedAt())
                .updatedAt(c.getUpdatedAt())

                // Receipt details
                .receiptAttached(c.getReceipt() != null)
                .receiptId(c.getReceipt() != null
                        ? c.getReceipt().getId()
                        : null)

                .build();
    }
}