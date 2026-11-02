package com.expensemanagement.controller;

import com.expensemanagement.dto.request.ClaimRequest;
import com.expensemanagement.dto.response.Response;
import com.expensemanagement.enums.ClaimStatus;
import com.expensemanagement.security.CustomUserDetails;
import com.expensemanagement.service.impl.ClaimServiceImpl;
import jakarta.validation.Valid;
import org.springframework.http.*;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/claims")
@PreAuthorize("hasRole('EMPLOYEE')")
public class ClaimController {
    private final ClaimServiceImpl service;

    public ClaimController(ClaimServiceImpl s) {
        service = s;
    }

    @PostMapping
    public ResponseEntity<Response> create(@AuthenticationPrincipal CustomUserDetails u, @Valid @RequestBody ClaimRequest r) {
        return ResponseEntity.status(201).body(Response.success("Claim created successfully.", service.create(u.getId(), r)));
    }

    @GetMapping
    public ResponseEntity<Response> list(@AuthenticationPrincipal CustomUserDetails u, @RequestParam(required = false) ClaimStatus status, @RequestParam(required = false) String search, @RequestParam(defaultValue = "0") int page, @RequestParam(defaultValue = "10") int size) {
        return ResponseEntity.ok(Response.success("Claims fetched successfully.", service.list(u.getId(), status, search, page, size)));
    }

    @GetMapping("/my-claims")
    public ResponseEntity<Response> myClaims(@AuthenticationPrincipal CustomUserDetails u, @RequestParam(required = false) ClaimStatus status, @RequestParam(required = false) String search, @RequestParam(defaultValue = "0") int page, @RequestParam(defaultValue = "10") int size) {
        return list(u, status, search, page, size);
    }

    @GetMapping("/{claimId}")
    public ResponseEntity<Response> get(@AuthenticationPrincipal CustomUserDetails u, @PathVariable UUID claimId) {
        return ResponseEntity.ok(Response.success("Claim details fetched.", service.get(u.getId(), claimId)));
    }

    @PutMapping("/{claimId}")
    public ResponseEntity<Response> update(@AuthenticationPrincipal CustomUserDetails u, @PathVariable UUID claimId, @Valid @RequestBody ClaimRequest r) {
        return ResponseEntity.ok(Response.success("Claim updated successfully.", service.update(u.getId(), claimId, r)));
    }

    @DeleteMapping("/{claimId}")
    public ResponseEntity<Response> delete(@AuthenticationPrincipal CustomUserDetails u, @PathVariable UUID claimId) {
        service.delete(u.getId(), claimId);
        return ResponseEntity.ok(Response.success("Claim deleted successfully.", null));
    }

    @PatchMapping("/{claimId}/submit")
    public ResponseEntity<Response> submit(@AuthenticationPrincipal CustomUserDetails u, @PathVariable UUID claimId) {
        return ResponseEntity.ok(Response.success("Claim submitted successfully.", service.submit(u.getId(), claimId)));
    }

    @GetMapping("/{claimId}/timeline")
    public ResponseEntity<Response> timeline(@AuthenticationPrincipal CustomUserDetails u, @PathVariable UUID claimId) {
        return ResponseEntity.ok(Response.success("Claim timeline fetched.", service.timeline(u.getId(), claimId)));
    }
}
