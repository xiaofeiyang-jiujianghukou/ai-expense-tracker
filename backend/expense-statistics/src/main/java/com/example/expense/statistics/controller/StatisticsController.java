package com.example.expense.statistics.controller;

import com.example.expense.common.ApiResponse;
import com.example.expense.statistics.dto.MonthlyStatsVO;
import com.example.expense.statistics.manager.StatisticsManager;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/statistics")
@RequiredArgsConstructor
public class StatisticsController {

    private final StatisticsManager statisticsManager;

    @GetMapping("/monthly")
    public ApiResponse<MonthlyStatsVO> monthly(
            @RequestParam int year,
            @RequestParam int month,
            Authentication auth) {
        Long userId = (Long) auth.getPrincipal();
        return ApiResponse.success(statisticsManager.getMonthlyStats(userId, year, month));
    }
}
