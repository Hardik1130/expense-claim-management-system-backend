package com.expensemanagement.service.impl;

import com.expensemanagement.dto.request.CategoryRequest;
import com.expensemanagement.dto.response.CategoryResponse;
import com.expensemanagement.entity.Category;
import com.expensemanagement.exception.*;
import com.expensemanagement.mapper.CategoryMapper;
import com.expensemanagement.repository.CategoryRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

@Service
@Transactional
public class CategoryServiceImpl implements com.expensemanagement.service.CategoryService {
    private final CategoryRepository repo;
    private final CategoryMapper mapper;

    public CategoryServiceImpl(CategoryRepository r, CategoryMapper m) {
        repo = r;
        mapper = m;
    }

    @Transactional(readOnly = true)
    public List<CategoryResponse> list() {
        return repo.findAllByActiveTrueOrderByNameAsc().stream().map(mapper::toResponse).toList();
    }

    @Transactional(readOnly = true)
    public CategoryResponse get(UUID id) {
        return mapper.toResponse(find(id));
    }

    public CategoryResponse create(CategoryRequest r) {
        if (repo.existsByNameIgnoreCase(r.getName().trim()))
            throw new UserAlreadyExistsException("Category already exists.");
        return mapper.toResponse(repo.save(Category.builder().name(r.getName().trim()).active(true).build()));
    }

    public CategoryResponse update(UUID id, CategoryRequest r) {
        Category c = find(id);
        repo.findByNameIgnoreCase(r.getName().trim()).filter(x -> !x.getId().equals(id)).ifPresent(x -> {
            throw new UserAlreadyExistsException("Category name already exists.");
        });
        c.setName(r.getName().trim());
        return mapper.toResponse(repo.save(c));
    }

    public void deactivate(UUID id) {
        Category c = find(id);
        c.setActive(false);
        repo.save(c);
    }

    private Category find(UUID id) {
        return repo.findById(id).orElseThrow(() -> new ResourceNotFoundException("Category not found."));
    }
}
