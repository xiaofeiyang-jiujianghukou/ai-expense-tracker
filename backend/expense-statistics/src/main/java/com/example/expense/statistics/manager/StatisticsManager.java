package com.example.expense.statistics.manager;

import com.example.expense.category.entity.Category;
import com.example.expense.category.service.CategoryService;
import com.example.expense.statistics.dto.MonthlyStatsVO;
import com.example.expense.statistics.service.StatisticsService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;

@Component
@RequiredArgsConstructor
public class StatisticsManager {

    private final StatisticsService statisticsService;
    private final CategoryService categoryService;

    public MonthlyStatsVO getMonthlyStats(Long userId, int year, int month) {
        BigDecimal income = statisticsService.sumByType(userId, year, month, "INCOME");
        BigDecimal expense = statisticsService.sumByType(userId, year, month, "EXPENSE");
        BigDecimal balance = income.subtract(expense);

        Map<Long, BigDecimal> categorySum = statisticsService.sumByCategory(userId, year, month, "EXPENSE");

        List<MonthlyStatsVO.CategorySummary> breakdown = new ArrayList<>();
        for (Map.Entry<Long, BigDecimal> entry : categorySum.entrySet()) {
            Category category = categoryService.findById(entry.getKey());
            BigDecimal pct = expense.compareTo(BigDecimal.ZERO) > 0
                    ? entry.getValue().multiply(BigDecimal.valueOf(100)).divide(expense, 1, RoundingMode.HALF_UP)
                    : BigDecimal.ZERO;

            breakdown.add(MonthlyStatsVO.CategorySummary.builder()
                    .categoryId(entry.getKey())
                    .categoryName(category.getName())
                    .amount(entry.getValue())
                    .percentage(pct)
                    .build());
        }
        breakdown.sort(Comparator.comparing(MonthlyStatsVO.CategorySummary::getAmount).reversed());

        return MonthlyStatsVO.builder()
                .year(year)
                .month(month)
                .income(income)
                .expense(expense)
                .balance(balance)
                .categoryBreakdown(breakdown)
                .build();
    }
}
