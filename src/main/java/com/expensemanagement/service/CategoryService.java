package com.expensemanagement.service;

import com.expensemanagement.dto.request.CategoryRequest;
import com.expensemanagement.dto.response.CategoryResponse;

import java.util.*;

public interface CategoryService {
    List<CategoryResponse> list();

    CategoryResponse get(UUID id);

    CategoryResponse create(CategoryRequest request);

    CategoryResponse update(UUID id, CategoryRequest request);

    void deactivate(UUID id);
}