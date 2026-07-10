package com.example.expense.category.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.example.expense.category.dto.CategoryRequest;
import com.example.expense.category.entity.Category;
import com.example.expense.category.mapper.CategoryMapper;
import com.example.expense.common.exception.BusinessException;
import com.example.expense.common.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class CategoryService {

    private static final Map<String, List<String>> DEFAULT_CATEGORIES = Map.of(
            "INCOME", List.of("工资", "奖金", "投资"),
            "EXPENSE", List.of("餐饮", "交通", "购物", "娱乐", "住房")
    );

    private final CategoryMapper categoryMapper;

    public Category create(CategoryRequest request, Long userId) {
        Category category = new Category();
        category.setUserId(userId);
        category.setName(request.getName());
        category.setType(request.getType());
        categoryMapper.insert(category);
        return category;
    }

    public List<Category> listByUser(Long userId, String type) {
        LambdaQueryWrapper<Category> wrapper = new LambdaQueryWrapper<Category>()
                .eq(Category::getUserId, userId);
        if (type != null && !type.isBlank()) {
            wrapper.eq(Category::getType, type);
        }
        wrapper.orderByAsc(Category::getCreatedTime);
        return categoryMapper.selectList(wrapper);
    }

    public Category update(Long id, CategoryRequest request, Long userId) {
        Category category = findById(id);
        if (!category.getUserId().equals(userId)) {
            throw new BusinessException(ErrorCode.FORBIDDEN);
        }
        category.setName(request.getName());
        category.setType(request.getType());
        categoryMapper.updateById(category);
        return category;
    }

    public void delete(Long id, Long userId) {
        Category category = findById(id);
        if (!category.getUserId().equals(userId)) {
            throw new BusinessException(ErrorCode.FORBIDDEN);
        }
        categoryMapper.deleteById(id);
    }

    public Category findById(Long id) {
        Category category = categoryMapper.selectById(id);
        if (category == null) {
            throw new BusinessException(ErrorCode.CATEGORY_NOT_FOUND);
        }
        return category;
    }

    public void initDefaultCategories(Long userId) {
        DEFAULT_CATEGORIES.forEach((type, names) -> {
            for (String name : names) {
                Category category = new Category();
                category.setUserId(userId);
                category.setName(name);
                category.setType(type);
                categoryMapper.insert(category);
            }
        });
    }
}
