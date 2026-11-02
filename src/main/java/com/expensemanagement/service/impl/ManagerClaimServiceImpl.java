package com.expensemanagement.service.impl;

import com.expensemanagement.dto.request.ReviewClaimRequest;
import com.expensemanagement.dto.response.*;
import com.expensemanagement.entity.*;
import com.expensemanagement.enums.*;
import com.expensemanagement.exception.*;
import com.expensemanagement.mapper.ClaimMapper;
import com.expensemanagement.repository.*;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.*;

@Service
@Transactional
public class ManagerClaimServiceImpl implements com.expensemanagement.service.ManagerClaimService {
    private final ClaimRepository claims;
    private final UserRepository users;
    private final ClaimAuditLogRepository audits;
    private final ClaimMapper mapper;

    public ManagerClaimServiceImpl(ClaimRepository c, UserRepository u, ClaimAuditLogRepository a, ClaimMapper m) {
        claims = c;
        users = u;
        audits = a;
        mapper = m;
    }

    @Transactional(readOnly = true)
    public PageResponse<ClaimResponse> list(ClaimStatus s, UUID uid, LocalDate from, LocalDate to, String q, int page, int size) {
        Pageable p = PageRequest.of(Math.max(0, page), Math.min(Math.max(1, size), 100), Sort.by(Sort.Direction.DESC, "createdAt"));
        Page<Claim> x = claims.searchManager(s, uid, from, to, (q == null || q.isBlank()) ? null : q.trim(), p);
        return PageResponse.<ClaimResponse>builder().content(x.getContent().stream().map(mapper::toResponse).toList()).page(x.getNumber()).size(x.getSize()).totalElements(x.getTotalElements()).totalPages(x.getTotalPages()).last(x.isLast()).build();
    }

    @Transactional(readOnly = true)
    public ClaimResponse get(UUID id) {
        return mapper.toResponse(claim(id));
    }

    public ClaimResponse approve(UUID mid, UUID id, ReviewClaimRequest r) {
        Claim c = claim(id);
        reviewable(c);
        User manager = user(mid);
        c.setStatus(ClaimStatus.APPROVED);
        c.setManagerComment(r == null ? null : r.getComment());
        c.setReviewedBy(manager);
        c.setReviewedAt(java.time.LocalDateTime.now());
        c = claims.save(c);
        audit(c, manager, AuditAction.APPROVED, "PENDING", "APPROVED", c.getManagerComment());
        return mapper.toResponse(c);
    }

    public ClaimResponse reject(UUID mid, UUID id, ReviewClaimRequest r) {
        if (r == null || r.getComment() == null || r.getComment().isBlank())
            throw new BadRequestException("A rejection comment is mandatory.");
        Claim c = claim(id);
        reviewable(c);
        User manager = user(mid);
        c.setStatus(ClaimStatus.REJECTED);
        c.setManagerComment(r.getComment().trim());
        c.setReviewedBy(manager);
        c.setReviewedAt(java.time.LocalDateTime.now());
        c = claims.save(c);
        audit(c, manager, AuditAction.REJECTED, "PENDING", "REJECTED", c.getManagerComment());
        return mapper.toResponse(c);
    }

    @Transactional(readOnly = true)
    public List<AuditLogResponse> timeline(UUID id) {
        Claim c = claim(id);
        return audits.findAllByClaimIdOrderByCreatedAtDesc(id).stream().map(a -> AuditLogResponse.builder().id(a.getId()).performedBy(a.getPerformedBy().getName()).action(a.getAction()).oldValue(a.getOldValue()).newValue(a.getNewValue()).comment(a.getComment()).createdAt(a.getCreatedAt()).build()).toList();
    }

    private Claim claim(UUID id) {
        return claims.findById(id).orElseThrow(() -> new ResourceNotFoundException("Claim not found."));
    }

    private User user(UUID id) {
        return users.findById(id).orElseThrow(() -> new ResourceNotFoundException("Manager not found."));
    }

    private void reviewable(Claim c) {
        if (c.getStatus() != ClaimStatus.PENDING) throw new BadRequestException("Only pending claims can be reviewed.");
    }

    private void audit(Claim c, User u, AuditAction a, String o, String n, String comment) {
        audits.save(ClaimAuditLog.builder().claim(c).performedBy(u).action(a).oldValue(o).newValue(n).comment(comment).build());
    }
}
