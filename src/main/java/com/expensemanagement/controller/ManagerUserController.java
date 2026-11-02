package com.expensemanagement.controller;

import com.expensemanagement.dto.request.UserStatusRequest;
import com.expensemanagement.dto.response.Response;
import com.expensemanagement.service.impl.UserManagementServiceImpl;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/manager/users")
@PreAuthorize("hasRole('MANAGER')")
public class ManagerUserController {
    private final UserManagementServiceImpl service;

    public ManagerUserController(UserManagementServiceImpl s) {
        service = s;
    }

    @GetMapping
    public ResponseEntity<Response> list(@RequestParam(defaultValue = "0") int page, @RequestParam(defaultValue = "10") int size) {
        return ResponseEntity.ok(Response.success("Employees fetched.", service.list(page, size)));
    }

    @GetMapping("/{userId}")
    public ResponseEntity<Response> get(@PathVariable UUID userId) {
        return ResponseEntity.ok(Response.success("Employee fetched.", service.get(userId)));
    }

    @PatchMapping("/{userId}/status")
    public ResponseEntity<Response> status(@PathVariable UUID userId, @Valid @RequestBody UserStatusRequest r) {
        return ResponseEntity.ok(Response.success("Employee status updated.", service.updateStatus(userId, r)));
    }
}
