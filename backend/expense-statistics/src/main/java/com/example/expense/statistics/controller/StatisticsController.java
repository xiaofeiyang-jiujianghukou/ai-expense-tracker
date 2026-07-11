package com.example.expense.statistics.controller;

import com.example.expense.common.ApiResponse;
import com.example.expense.statistics.dto.DailyVO;
import com.example.expense.statistics.dto.MonthlyStatsVO;
import com.example.expense.statistics.dto.TrendVO;
import com.example.expense.statistics.manager.StatisticsManager;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import com.example.expense.common.util.SecurityUtil;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/statistics")
@RequiredArgsConstructor
public class StatisticsController {

    private final StatisticsManager statisticsManager;

    @Data
    public static class MonthlyRequest {
        private int year;
        private int month;
    }

    @Data
    public static class TrendRequest {
        private int months = 6;
    }

    @PostMapping("/monthly")
    public ApiResponse<MonthlyStatsVO> monthly(@RequestBody MonthlyRequest request) {
        Long userId = SecurityUtil.getCurrentUserId();
        return ApiResponse.success(
                statisticsManager.getMonthlyStats(userId, request.getYear(), request.getMonth()));
    }

    @PostMapping("/trend")
    public ApiResponse<TrendVO> trend(@RequestBody TrendRequest request) {
        Long userId = SecurityUtil.getCurrentUserId();
        return ApiResponse.success(
                statisticsManager.getMonthlyTrend(userId, request.getMonths()));
    }

    @PostMapping("/daily")
    public ApiResponse<DailyVO> daily(@RequestBody MonthlyRequest request) {
        Long userId = SecurityUtil.getCurrentUserId();
        return ApiResponse.success(
                statisticsManager.getDailyDistribution(userId, request.getYear(), request.getMonth()));
    }
}
