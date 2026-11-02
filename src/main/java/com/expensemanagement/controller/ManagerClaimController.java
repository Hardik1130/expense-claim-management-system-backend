package com.expensemanagement.controller;

import com.expensemanagement.dto.request.ReviewClaimRequest;
import com.expensemanagement.dto.response.Response;
import com.expensemanagement.enums.ClaimStatus;
import com.expensemanagement.security.CustomUserDetails;
import com.expensemanagement.service.impl.ManagerClaimServiceImpl;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/manager/claims")
@PreAuthorize("hasRole('MANAGER')")
public class ManagerClaimController {
    private final ManagerClaimServiceImpl service;

    public ManagerClaimController(ManagerClaimServiceImpl s) {
        service = s;
    }

    @GetMapping
    public ResponseEntity<Response> list(@RequestParam(required = false) ClaimStatus status, @RequestParam(required = false) UUID employeeId, @RequestParam(required = false) LocalDate fromDate, @RequestParam(required = false) LocalDate toDate, @RequestParam(required = false) String search, @RequestParam(defaultValue = "0") int page, @RequestParam(defaultValue = "10") int size) {
        return ResponseEntity.ok(Response.success("Claims fetched successfully.", service.list(status, employeeId, fromDate, toDate, search, page, size)));
    }

    @GetMapping("/pending")
    public ResponseEntity<Response> pending(@RequestParam(defaultValue = "0") int page, @RequestParam(defaultValue = "10") int size) {
        return ResponseEntity.ok(Response.success("Pending claims fetched.", service.list(ClaimStatus.PENDING, null, null, null, null, page, size)));
    }

    @GetMapping("/{claimId}")
    public ResponseEntity<Response> get(@PathVariable UUID claimId) {
        return ResponseEntity.ok(Response.success("Claim details fetched.", service.get(claimId)));
    }

    @GetMapping("/{claimId}/timeline")
    public ResponseEntity<Response> timeline(@PathVariable UUID claimId) {
        return ResponseEntity.ok(Response.success("Claim timeline fetched.", service.timeline(claimId)));
    }

    @PatchMapping("/{claimId}/approve")
    public ResponseEntity<Response> approve(@AuthenticationPrincipal CustomUserDetails u, @PathVariable UUID claimId, @RequestBody(required = false) ReviewClaimRequest r) {
        return ResponseEntity.ok(Response.success("Claim approved successfully.", service.approve(u.getId(), claimId, r)));
    }

    @PatchMapping("/{claimId}/reject")
    public ResponseEntity<Response> reject(@AuthenticationPrincipal CustomUserDetails u, @PathVariable UUID claimId, @Valid @RequestBody ReviewClaimRequest r) {
        return ResponseEntity.ok(Response.success("Claim rejected successfully.", service.reject(u.getId(), claimId, r)));
    }
}
