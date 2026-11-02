package com.expensemanagement.service.impl;

import com.expensemanagement.dto.response.UserSummaryResponse;
import com.expensemanagement.dto.request.UserStatusRequest;
import com.expensemanagement.entity.User;
import com.expensemanagement.enums.Role;
import com.expensemanagement.exception.ResourceNotFoundException;
import com.expensemanagement.repository.UserRepository;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

@Service
@Transactional
public class UserManagementServiceImpl implements com.expensemanagement.service.UserManagementService {
    private final UserRepository repo;

    public UserManagementServiceImpl(UserRepository r) {
        repo = r;
    }

    @Transactional(readOnly = true)
    public Page<UserSummaryResponse> list(int page, int size) {
        return repo.findByRole(Role.EMPLOYEE, PageRequest.of(Math.max(0, page), Math.min(Math.max(size, 1), 100), Sort.by(Sort.Direction.DESC, "createdAt"))).map(this::map);
    }

    @Transactional(readOnly = true)
    public UserSummaryResponse get(UUID id) {
        return map(find(id));
    }

    public UserSummaryResponse updateStatus(UUID id, UserStatusRequest r) {
        User u = find(id);
        u.setActive(r.getActive());
        return map(repo.save(u));
    }

    private User find(UUID id) {
        User u = repo.findById(id).orElseThrow(() -> new ResourceNotFoundException("User not found."));
        if (u.getRole() != Role.EMPLOYEE) throw new ResourceNotFoundException("Employee not found.");
        return u;
    }

    private UserSummaryResponse map(User u) {
        return UserSummaryResponse.builder().id(u.getId()).name(u.getName()).email(u.getEmail()).role(u.getRole()).active(u.isActive()).createdAt(u.getCreatedAt()).build();
    }
}
