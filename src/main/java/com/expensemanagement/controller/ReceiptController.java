package com.expensemanagement.controller;

import com.expensemanagement.dto.response.Response;
import com.expensemanagement.security.CustomUserDetails;
import com.expensemanagement.service.impl.ReceiptServiceImpl;
import org.springframework.core.io.Resource;
import org.springframework.http.*;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/claims/{claimId}/receipts")
public class ReceiptController {
    private final ReceiptServiceImpl service;

    public ReceiptController(ReceiptServiceImpl s) {
        service = s;
    }

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @PreAuthorize("hasRole('EMPLOYEE')")
    public ResponseEntity<Response> upload(@AuthenticationPrincipal CustomUserDetails u, @PathVariable UUID claimId, @RequestPart("file") MultipartFile file) {
        return ResponseEntity.status(201).body(Response.success("Receipt uploaded.", service.upload(u.getId(), claimId, file)));
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('EMPLOYEE','MANAGER')")
    public ResponseEntity<Response> list(@AuthenticationPrincipal CustomUserDetails u, @PathVariable UUID claimId) {
        boolean manager = u.getAuthorities().stream().anyMatch(a -> a.getAuthority().equals("ROLE_MANAGER"));
        return ResponseEntity.ok(Response.success("Receipts fetched.", service.list(u.getId(), claimId, manager)));
    }

    @GetMapping("/{receiptId}")
    @PreAuthorize("hasAnyRole('EMPLOYEE','MANAGER')")
    public ResponseEntity<Resource> download(@AuthenticationPrincipal CustomUserDetails u, @PathVariable UUID claimId, @PathVariable UUID receiptId) {
        boolean manager = u.getAuthorities().stream().anyMatch(a -> a.getAuthority().equals("ROLE_MANAGER"));
        var f = service.download(u.getId(), receiptId, manager);
        return ResponseEntity.ok().contentType(MediaType.parseMediaType(f.contentType())).header(HttpHeaders.CONTENT_DISPOSITION, ContentDisposition.attachment().filename(f.filename()).build().toString()).body(f.resource());
    }

    @DeleteMapping
    @PreAuthorize("hasRole('EMPLOYEE')")
    public ResponseEntity<Response> delete(@AuthenticationPrincipal CustomUserDetails u, @PathVariable UUID claimId) {
        service.delete(u.getId(), claimId);
        return ResponseEntity.ok(Response.success("Receipt deleted.", null));
    }
}
