package com.example.expense.category.manager;

import com.example.expense.category.dto.CategoryRequest;
import com.example.expense.category.dto.CategoryVO;
import com.example.expense.category.entity.Category;
import com.example.expense.category.service.CategoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class CategoryManager {

    private final CategoryService categoryService;

    public CategoryVO create(CategoryRequest request, Long userId) {
        Category category = categoryService.create(request, userId);
        return toVO(category);
    }

    public List<CategoryVO> list(Long userId, String type) {
        return categoryService.listByUser(userId, type).stream()
                .map(this::toVO)
                .toList();
    }

    public CategoryVO update(Long id, CategoryRequest request, Long userId) {
        Category category = categoryService.update(id, request, userId);
        return toVO(category);
    }

    public void delete(Long id, Long userId) {
        categoryService.delete(id, userId);
    }

    private CategoryVO toVO(Category category) {
        return CategoryVO.builder()
                .id(category.getId())
                .name(category.getName())
                .type(category.getType())
                .createdTime(category.getCreatedTime())
                .build();
    }
}
