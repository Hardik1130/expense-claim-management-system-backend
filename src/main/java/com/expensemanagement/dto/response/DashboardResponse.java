package com.expensemanagement.dto.response;

import lombok.*;

import java.math.BigDecimal;
import java.util.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DashboardResponse {
    private long totalClaims;
    private long pendingCount;
    private long approvedCount;
    private long rejectedCount;
    private BigDecimal totalClaimedAmount;
    private BigDecimal pendingAmount;
    private BigDecimal approvedAmount;
    private BigDecimal rejectedAmount;
    private List<Map<String, Object>> categoryTotals;
    private List<Map<String, Object>> monthlySummary;
}
