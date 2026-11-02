package com.expensemanagement.controller;

import com.expensemanagement.dto.response.Response;
import com.expensemanagement.service.SystemStatusService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/system")
public class SystemController {
    private final SystemStatusService systemStatusService;

    public SystemController(SystemStatusService systemStatusService) {
        this.systemStatusService = systemStatusService;
    }

    @GetMapping("/ping")
    public ResponseEntity<Response> ping() {
        return ResponseEntity.ok(Response.success("Application is reachable.", "pong"));
    }

    @GetMapping("/status")
    public ResponseEntity<Response> status() {
        return ResponseEntity.ok(Response.success(
                "System status fetched successfully.", systemStatusService.getStatus()));
    }
}
