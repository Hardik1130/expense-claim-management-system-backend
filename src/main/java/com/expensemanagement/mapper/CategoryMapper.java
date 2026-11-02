package com.expensemanagement.mapper;

import com.expensemanagement.dto.response.CategoryResponse;
import com.expensemanagement.entity.Category;
import org.springframework.stereotype.Component;

@Component
public class CategoryMapper {
    public CategoryResponse toResponse(Category c) {
        return CategoryResponse.builder().id(c.getId()).name(c.getName()).active(c.isActive()).createdAt(c.getCreatedAt()).build();
    }
}
