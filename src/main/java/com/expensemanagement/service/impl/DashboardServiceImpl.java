package com.expensemanagement.service.impl;

import com.expensemanagement.dto.response.DashboardResponse;
import com.expensemanagement.entity.Claim;
import com.expensemanagement.enums.ClaimStatus;
import com.expensemanagement.repository.ClaimRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.YearMonth;
import java.util.*;
import java.util.stream.*;

@Service
public class DashboardServiceImpl implements com.expensemanagement.service.DashboardService {
    private final ClaimRepository repo;

    public DashboardServiceImpl(ClaimRepository r) {
        repo = r;
    }

    @Transactional(readOnly = true)
    public DashboardResponse employee(UUID uid) {
        return build(repo.findAllByUserId(uid));
    }

    @Transactional(readOnly = true)
    public DashboardResponse manager() {
        return build(repo.findAll());
    }

    private DashboardResponse build(List<Claim> claims) {
        BigDecimal total = sum(claims, null), pending = sum(claims, ClaimStatus.PENDING), approved = sum(claims, ClaimStatus.APPROVED), rejected = sum(claims, ClaimStatus.REJECTED);
        Map<String, BigDecimal> cats = claims.stream().collect(Collectors.groupingBy(c -> c.getCategory().getName(), Collectors.reducing(BigDecimal.ZERO, Claim::getAmount, BigDecimal::add)));
        List<Map<String, Object>> category = cats.entrySet().stream().map(e -> Map.<String, Object>of("category", e.getKey(), "amount", e.getValue())).toList();
        Map<String, BigDecimal> months = claims.stream().collect(Collectors.groupingBy(c -> YearMonth.from(c.getExpenseDate()).toString(), Collectors.reducing(BigDecimal.ZERO, Claim::getAmount, BigDecimal::add)));
        List<Map<String, Object>> monthly = months.entrySet().stream().sorted(Map.Entry.comparingByKey()).map(e -> Map.<String, Object>of("month", e.getKey(), "amount", e.getValue())).toList();
        return DashboardResponse.builder().totalClaims(claims.size()).pendingCount(count(claims, ClaimStatus.PENDING)).approvedCount(count(claims, ClaimStatus.APPROVED)).rejectedCount(count(claims, ClaimStatus.REJECTED)).totalClaimedAmount(total).pendingAmount(pending).approvedAmount(approved).rejectedAmount(rejected).categoryTotals(category).monthlySummary(monthly).build();
    }

    private BigDecimal sum(List<Claim> c, ClaimStatus s) {
        return c.stream().filter(x -> s == null || x.getStatus() == s).map(Claim::getAmount).reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    private long count(List<Claim> c, ClaimStatus s) {
        return c.stream().filter(x -> x.getStatus() == s).count();
    }
}
