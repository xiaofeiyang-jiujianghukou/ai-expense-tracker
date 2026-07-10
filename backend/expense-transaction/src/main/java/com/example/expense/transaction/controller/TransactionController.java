package com.example.expense.transaction.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.expense.common.ApiResponse;
import com.example.expense.transaction.dto.TransactionRequest;
import com.example.expense.transaction.dto.TransactionVO;
import com.example.expense.transaction.manager.TransactionManager;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;

@RestController
@RequestMapping("/api/transactions")
@RequiredArgsConstructor
public class TransactionController {

    private final TransactionManager transactionManager;

    @PostMapping
    public ApiResponse<TransactionVO> create(@Valid @RequestBody TransactionRequest request,
                                             Authentication auth) {
        Long userId = (Long) auth.getPrincipal();
        return ApiResponse.success(transactionManager.create(request, userId));
    }

    @GetMapping
    public ApiResponse<Page<TransactionVO>> list(
            @RequestParam(required = false) String type,
            @RequestParam(required = false) Long categoryId,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size,
            Authentication auth) {
        Long userId = (Long) auth.getPrincipal();
        return ApiResponse.success(transactionManager.page(userId, type, categoryId,
                startDate, endDate, page, size));
    }

    @GetMapping("/{id}")
    public ApiResponse<TransactionVO> getById(@PathVariable Long id, Authentication auth) {
        Long userId = (Long) auth.getPrincipal();
        return ApiResponse.success(transactionManager.getById(id, userId));
    }

    @PutMapping("/{id}")
    public ApiResponse<TransactionVO> update(@PathVariable Long id,
                                             @Valid @RequestBody TransactionRequest request,
                                             Authentication auth) {
        Long userId = (Long) auth.getPrincipal();
        return ApiResponse.success(transactionManager.update(id, request, userId));
    }

    @DeleteMapping("/{id}")
    public ApiResponse<Void> delete(@PathVariable Long id, Authentication auth) {
        Long userId = (Long) auth.getPrincipal();
        transactionManager.delete(id, userId);
        return ApiResponse.success();
    }
}
