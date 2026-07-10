package com.example.expense.category.controller;

import com.example.expense.category.dto.CategoryRequest;
import com.example.expense.category.dto.CategoryVO;
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
    public ApiResponse<CategoryVO> create(@Valid @RequestBody CategoryRequest request,
                                          Authentication auth) {
        Long userId = (Long) auth.getPrincipal();
        return ApiResponse.success(categoryManager.create(request, userId));
    }

    @GetMapping
    public ApiResponse<List<CategoryVO>> list(@RequestParam(required = false) String type,
                                              Authentication auth) {
        Long userId = (Long) auth.getPrincipal();
        return ApiResponse.success(categoryManager.list(userId, type));
    }

    @PutMapping("/{id}")
    public ApiResponse<CategoryVO> update(@PathVariable Long id,
                                          @Valid @RequestBody CategoryRequest request,
                                          Authentication auth) {
        Long userId = (Long) auth.getPrincipal();
        return ApiResponse.success(categoryManager.update(id, request, userId));
    }

    @DeleteMapping("/{id}")
    public ApiResponse<Void> delete(@PathVariable Long id, Authentication auth) {
        Long userId = (Long) auth.getPrincipal();
        categoryManager.delete(id, userId);
        return ApiResponse.success();
    }
}
