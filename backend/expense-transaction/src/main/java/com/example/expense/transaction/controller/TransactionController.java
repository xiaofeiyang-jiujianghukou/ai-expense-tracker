package com.example.expense.transaction.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.expense.common.ApiResponse;
import com.example.expense.transaction.dto.*;
import com.example.expense.transaction.service.TransactionService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/transactions")
@RequiredArgsConstructor
public class TransactionController {

    private final TransactionService transactionService;

    @PostMapping
    public ApiResponse<Void> create(@Valid @RequestBody TransactionRequest request,
                                     Authentication auth) {
        Long userId = (Long) auth.getPrincipal();
        transactionService.create(request, userId);
        return ApiResponse.success();
    }

    @PostMapping("/list")
    public ApiResponse<Page<TransactionVO>> list(@RequestBody TransactionListRequest request,
                                                  Authentication auth) {
        Long userId = (Long) auth.getPrincipal();
        return ApiResponse.success(transactionService.page(userId, request.getType(),
                request.getCategoryId(), request.getStartDate(), request.getEndDate(),
                request.getPage(), request.getSize()));
    }

    @GetMapping("/{id}")
    public ApiResponse<TransactionVO> getById(@PathVariable Long id, Authentication auth) {
        Long userId = (Long) auth.getPrincipal();
        return ApiResponse.success(transactionService.getById(id, userId));
    }

    @PostMapping("/update")
    public ApiResponse<Void> update(@Valid @RequestBody TransactionUpdateRequest request,
                                     Authentication auth) {
        Long userId = (Long) auth.getPrincipal();
        transactionService.update(request.getId(), request, userId);
        return ApiResponse.success();
    }

    @PostMapping("/delete")
    public ApiResponse<Void> delete(@Valid @RequestBody TransactionDeleteRequest request,
                                     Authentication auth) {
        Long userId = (Long) auth.getPrincipal();
        transactionService.delete(request.getId(), userId);
        return ApiResponse.success();
    }
}
