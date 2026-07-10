package com.example.expense.bill.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.expense.common.ApiResponse;
import com.example.expense.bill.dto.*;
import com.example.expense.bill.service.BillService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/bills")
@RequiredArgsConstructor
public class BillController {

    private final BillService BillService;

    @PostMapping
    public ApiResponse<Void> create(@Valid @RequestBody BillRequest request,
                                     Authentication auth) {
        Long userId = (Long) auth.getPrincipal();
        BillService.create(request, userId);
        return ApiResponse.success();
    }

    @PostMapping("/list")
    public ApiResponse<Page<BillVO>> list(@RequestBody BillListRequest request,
                                                  Authentication auth) {
        Long userId = (Long) auth.getPrincipal();
        return ApiResponse.success(BillService.page(userId, request.getType(),
                request.getCategoryId(), request.getStartDate(), request.getEndDate(),
                request.getPage(), request.getSize()));
    }

    @GetMapping("/{id}")
    public ApiResponse<BillVO> getById(@PathVariable Long id, Authentication auth) {
        Long userId = (Long) auth.getPrincipal();
        return ApiResponse.success(BillService.getById(id, userId));
    }

    @PostMapping("/update")
    public ApiResponse<Void> update(@Valid @RequestBody BillUpdateRequest request,
                                     Authentication auth) {
        Long userId = (Long) auth.getPrincipal();
        BillService.update(request.getId(), request, userId);
        return ApiResponse.success();
    }

    @PostMapping("/delete")
    public ApiResponse<Void> delete(@Valid @RequestBody BillDeleteRequest request,
                                     Authentication auth) {
        Long userId = (Long) auth.getPrincipal();
        BillService.delete(request.getId(), userId);
        return ApiResponse.success();
    }
}
