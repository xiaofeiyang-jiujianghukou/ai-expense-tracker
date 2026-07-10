package com.example.expense.category.controller;

import com.example.expense.category.dto.*;
import com.example.expense.category.manager.CategoryManager;
import com.example.expense.common.ApiResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/categories")
@RequiredArgsConstructor
public class CategoryController {

    private final CategoryManager categoryManager;

    @PostMapping
    public ApiResponse<Void> create(@Valid @RequestBody CategoryRequest request,
                                     Authentication auth) {
        Long userId = (Long) auth.getPrincipal();
        categoryManager.create(request, userId);
        return ApiResponse.success();
    }

    @PostMapping("/list")
    public ApiResponse<List<CategoryVO>> list(@RequestBody CategoryListRequest request,
                                              Authentication auth) {
        Long userId = (Long) auth.getPrincipal();
        return ApiResponse.success(categoryManager.list(userId, request.getType()));
    }

    @GetMapping("/{id}")
    public ApiResponse<CategoryVO> getById(@PathVariable Long id, Authentication auth) {
        Long userId = (Long) auth.getPrincipal();
        return ApiResponse.success(categoryManager.getById(id, userId));
    }

    @PostMapping("/update")
    public ApiResponse<Void> update(@Valid @RequestBody CategoryUpdateRequest request,
                                     Authentication auth) {
        Long userId = (Long) auth.getPrincipal();
        categoryManager.update(request, userId);
        return ApiResponse.success();
    }

    @PostMapping("/delete")
    public ApiResponse<Void> delete(@Valid @RequestBody CategoryDeleteRequest request,
                                     Authentication auth) {
        Long userId = (Long) auth.getPrincipal();
        categoryManager.delete(request.getId(), userId);
        return ApiResponse.success();
    }
}
