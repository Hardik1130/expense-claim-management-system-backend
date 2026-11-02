package com.expensemanagement.service.impl;

import com.expensemanagement.dto.request.ClaimRequest;
import com.expensemanagement.dto.response.*;
import com.expensemanagement.entity.*;
import com.expensemanagement.enums.*;
import com.expensemanagement.exception.*;
import com.expensemanagement.mapper.ClaimMapper;
import com.expensemanagement.repository.*;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

@Service
@Transactional
public class ClaimServiceImpl implements com.expensemanagement.service.ClaimService {
    private final ClaimRepository claims;
    private final UserRepository users;
    private final CategoryRepository categories;
    private final ClaimAuditLogRepository audits;
    private final ClaimMapper mapper;
    private final ClaimReceiptRepository receipts;

    public ClaimServiceImpl(
            ClaimRepository c,
            UserRepository u,
            CategoryRepository cat,
            ClaimAuditLogRepository a,
            ClaimReceiptRepository r,
            ClaimMapper m) {

        claims = c;
        users = u;
        categories = cat;
        audits = a;
        receipts = r;
        mapper = m;
    }

    public ClaimResponse create(UUID uid, ClaimRequest r) {
        User u = user(uid);
        Category cat = category(r.getCategoryId());
        Claim c = Claim.builder().user(u).category(cat).title(r.getTitle().trim()).amount(r.getAmount()).expenseDate(r.getExpenseDate()).description(r.getDescription()).status(ClaimStatus.PENDING).build();
        c = claims.save(c);
        audit(c, u, AuditAction.CREATED, null, "PENDING", null);
        return mapper.toResponse(c);
    }

    public ClaimResponse update(UUID uid, UUID id, ClaimRequest r) {
        Claim c = owned(uid, id);
        editable(c);
        String old = c.getTitle() + " | " + c.getAmount() + " | " + c.getCategory().getName();
        c.setTitle(r.getTitle().trim());
        c.setAmount(r.getAmount());
        c.setCategory(category(r.getCategoryId()));
        c.setExpenseDate(r.getExpenseDate());
        c.setDescription(r.getDescription());
        c = claims.save(c);
        audit(c, c.getUser(), AuditAction.UPDATED, old, c.getTitle() + " | " + c.getAmount(), null);
        return mapper.toResponse(c);
    }

    public void delete(UUID uid, UUID id) {

        Claim c = owned(uid, id);

        editable(c);

        // Delete receipt database record first
        receipts.deleteByClaimId(c.getId());

        // Delete audit history
        audits.deleteAllByClaimId(c.getId());

        // Finally delete claim
        claims.delete(c);
    }

    @Transactional(readOnly = true)
    public ClaimResponse get(UUID uid, UUID id) {
        return mapper.toResponse(owned(uid, id));
    }

    @Transactional(readOnly = true)
    public PageResponse<ClaimResponse> list(UUID uid, ClaimStatus status, String q, int page, int size) {
        Pageable p = PageRequest.of(Math.max(0, page), Math.min(Math.max(1, size), 100), Sort.by(Sort.Direction.DESC, "createdAt"));
        Page<Claim> result = claims.searchEmployee(uid, status, (q == null || q.isBlank()) ? null : q.trim(), p);
        return page(result);
    }

    public ClaimResponse submit(UUID uid, UUID id) {
        Claim c = owned(uid, id);
        editable(c);
        c.setStatus(ClaimStatus.PENDING);
        c = claims.save(c);
        audit(c, c.getUser(), AuditAction.UPDATED, "DRAFT", "PENDING", "Submitted for manager review");
        return mapper.toResponse(c);
    }

    public List<AuditLogResponse> timeline(UUID uid, UUID id) {
        Claim c = owned(uid, id);
        return audits.findAllByClaimIdOrderByCreatedAtDesc(c.getId()).stream().map(a -> AuditLogResponse.builder().id(a.getId()).performedBy(a.getPerformedBy().getName()).action(a.getAction()).oldValue(a.getOldValue()).newValue(a.getNewValue()).comment(a.getComment()).createdAt(a.getCreatedAt()).build()).toList();
    }

    public Claim owned(UUID uid, UUID id) {
        Claim c = claims.findById(id).orElseThrow(() -> new ResourceNotFoundException("Claim not found."));
        if (!c.getUser().getId().equals(uid))
            throw new ForbiddenOperationException("You cannot access another employee's claim.");
        return c;
    }

    private void editable(Claim c) {
        if (c.getStatus() != ClaimStatus.PENDING) throw new BadRequestException("Only pending claims can be modified.");
    }

    private User user(UUID id) {
        return users.findById(id).orElseThrow(() -> new ResourceNotFoundException("User not found."));
    }

    private Category category(UUID id) {
        Category c = categories.findById(id).orElseThrow(() -> new ResourceNotFoundException("Category not found."));
        if (!c.isActive()) throw new BadRequestException("Selected category is inactive.");
        return c;
    }

    private void audit(Claim c, User u, AuditAction a, String old, String n, String comment) {
        audits.save(ClaimAuditLog.builder().claim(c).performedBy(u).action(a).oldValue(old).newValue(n).comment(comment).build());
    }

    private PageResponse<ClaimResponse> page(Page<Claim> p) {
        return PageResponse.<ClaimResponse>builder().content(p.getContent().stream().map(mapper::toResponse).toList()).page(p.getNumber()).size(p.getSize()).totalElements(p.getTotalElements()).totalPages(p.getTotalPages()).last(p.isLast()).build();
    }
}
