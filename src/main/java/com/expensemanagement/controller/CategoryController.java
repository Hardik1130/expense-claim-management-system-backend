package com.expensemanagement.controller;

import com.expensemanagement.dto.request.CategoryRequest;
import com.expensemanagement.dto.response.Response;
import com.expensemanagement.service.impl.CategoryServiceImpl;
import jakarta.validation.Valid;
import org.springframework.http.*;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/categories")
public class CategoryController {
    private final CategoryServiceImpl service;

    public CategoryController(CategoryServiceImpl s) {
        service = s;
    }

    @GetMapping
    public ResponseEntity<Response> list() {
        return ResponseEntity.ok(Response.success("Categories fetched.", service.list()));
    }

    @GetMapping("/{id}")
    public ResponseEntity<Response> get(@PathVariable UUID id) {
        return ResponseEntity.ok(Response.success("Category fetched.", service.get(id)));
    }

    @PostMapping
    @PreAuthorize("hasRole('MANAGER')")
    public ResponseEntity<Response> create(@Valid @RequestBody CategoryRequest r) {
        return ResponseEntity.status(201).body(Response.success("Category created.", service.create(r)));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('MANAGER')")
    public ResponseEntity<Response> update(@PathVariable UUID id, @Valid @RequestBody CategoryRequest r) {
        return ResponseEntity.ok(Response.success("Category updated.", service.update(id, r)));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('MANAGER')")
    public ResponseEntity<Response> deactivate(@PathVariable UUID id) {
        service.deactivate(id);
        return ResponseEntity.ok(Response.success("Category deactivated.", null));
    }
}
