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

    public void create(CategoryRequest request, Long userId) {
        categoryService.create(request, userId);
    }

    public List<CategoryVO> list(Long userId, String type) {
        return categoryService.listByUser(userId, type).stream()
                .map(this::toVO)
                .toList();
    }

    public void update(Long id, CategoryRequest request, Long userId) {
        categoryService.update(id, request, userId);
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
