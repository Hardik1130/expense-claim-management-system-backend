package com.expensemanagement.mapper;

import com.expensemanagement.dto.response.UserResponse;
import com.expensemanagement.entity.User;
import org.springframework.stereotype.Component;

@Component
public class UserMapper {
    public UserResponse toResponse(User user) {
        return UserResponse.builder()
                .id(user.getId()).name(user.getName()).email(user.getEmail())
                .role(user.getRole()).active(user.isActive()).build();
    }
}
