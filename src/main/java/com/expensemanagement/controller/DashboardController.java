package com.expensemanagement.controller;

import com.expensemanagement.dto.response.Response;
import com.expensemanagement.security.CustomUserDetails;
import com.expensemanagement.service.impl.DashboardServiceImpl;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1")
public class DashboardController {
    private final DashboardServiceImpl service;

    public DashboardController(DashboardServiceImpl s) {
        service = s;
    }

    @GetMapping("/employee/dashboard")
    @PreAuthorize("hasRole('EMPLOYEE')")
    public ResponseEntity<Response> employee(@AuthenticationPrincipal CustomUserDetails u) {
        return ResponseEntity.ok(Response.success("Employee dashboard fetched.", service.employee(u.getId())));
    }

    @GetMapping("/manager/dashboard")
    @PreAuthorize("hasRole('MANAGER')")
    public ResponseEntity<Response> manager() {
        return ResponseEntity.ok(Response.success("Manager dashboard fetched.", service.manager()));
    }
}
