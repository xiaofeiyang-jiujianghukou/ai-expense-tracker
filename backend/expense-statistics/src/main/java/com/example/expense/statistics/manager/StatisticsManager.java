package com.example.expense.statistics.manager;

import com.example.expense.category.service.CategoryService;
import com.example.expense.common.exception.BusinessException;
import com.example.expense.statistics.dto.MonthlyStatsVO;
import com.example.expense.statistics.service.StatisticsService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import com.example.expense.bill.entity.Bill;
import com.example.expense.common.enums.BillType;
import com.example.expense.statistics.dto.DailyVO;
import com.example.expense.statistics.dto.TrendVO;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.*;

@Component
@RequiredArgsConstructor
public class StatisticsManager {

    private static final String UNKNOWN_CATEGORY = "未知";

    private final StatisticsService statisticsService;
    private final CategoryService categoryService;

    private String getCategoryName(Long categoryId) {
        try {
            return categoryService.findById(categoryId).getName();
        } catch (BusinessException e) {
            return UNKNOWN_CATEGORY;
        }
    }

    public MonthlyStatsVO getMonthlyStats(Long userId, int year, int month) {
        BigDecimal income = statisticsService.sumByType(userId, year, month, BillType.INCOME);
        BigDecimal expense = statisticsService.sumByType(userId, year, month, BillType.EXPENSE);
        BigDecimal balance = income.subtract(expense);

        Map<Long, BigDecimal> categorySum = statisticsService.sumByCategory(userId, year, month, BillType.EXPENSE);

        List<MonthlyStatsVO.CategorySummary> breakdown = new ArrayList<>();
        for (Map.Entry<Long, BigDecimal> entry : categorySum.entrySet()) {
            String categoryName = getCategoryName(entry.getKey());
            BigDecimal pct = expense.compareTo(BigDecimal.ZERO) > 0
                    ? entry.getValue().multiply(BigDecimal.valueOf(100)).divide(expense, 1, RoundingMode.HALF_UP)
                    : BigDecimal.ZERO;

            breakdown.add(MonthlyStatsVO.CategorySummary.builder()
                    .categoryId(entry.getKey())
                    .categoryName(categoryName)
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

    public TrendVO getMonthlyTrend(Long userId, int months) {
        LocalDate end = LocalDate.now();
        LocalDate start = end.minusMonths(months - 1).withDayOfMonth(1);

        List<Bill> bills = statisticsService.queryByDateRange(userId, start, end);

        // Group by year-month: key = "2026-07"
        Map<String, BigDecimal[]> monthlyData = new LinkedHashMap<>();
        // Initialize all months in range
        LocalDate cursor = start;
        while (!cursor.isAfter(end)) {
            String key = cursor.getYear() + "-" + String.format("%02d", cursor.getMonthValue());
            monthlyData.put(key, new BigDecimal[]{BigDecimal.ZERO, BigDecimal.ZERO});
            cursor = cursor.plusMonths(1);
        }

        for (Bill bill : bills) {
            String key = bill.getBillDate().getYear() + "-"
                    + String.format("%02d", bill.getBillDate().getMonthValue());
            BigDecimal[] data = monthlyData.get(key);
            if (data != null) {
                if (BillType.INCOME.name().equals(bill.getType())) {
                    data[0] = data[0].add(bill.getAmount());
                } else {
                    data[1] = data[1].add(bill.getAmount());
                }
            }
        }

        List<TrendVO.TrendPoint> points = new ArrayList<>();
        for (Map.Entry<String, BigDecimal[]> entry : monthlyData.entrySet()) {
            String[] parts = entry.getKey().split("-");
            int y = Integer.parseInt(parts[0]);
            int m = Integer.parseInt(parts[1]);
            BigDecimal inc = entry.getValue()[0];
            BigDecimal exp = entry.getValue()[1];
            points.add(TrendVO.TrendPoint.builder()
                    .year(y).month(m)
                    .income(inc).expense(exp)
                    .balance(inc.subtract(exp))
                    .build());
        }

        return TrendVO.builder().points(points).build();
    }

    public DailyVO getDailyDistribution(Long userId, int year, int month) {
        List<Bill> bills = statisticsService.queryByMonth(userId, year, month);

        int daysInMonth = LocalDate.of(year, month, 1).lengthOfMonth();
        BigDecimal[] incomeByDay = new BigDecimal[daysInMonth + 1];
        BigDecimal[] expenseByDay = new BigDecimal[daysInMonth + 1];
        for (int i = 1; i <= daysInMonth; i++) {
            incomeByDay[i] = BigDecimal.ZERO;
            expenseByDay[i] = BigDecimal.ZERO;
        }

        for (Bill bill : bills) {
            int day = bill.getBillDate().getDayOfMonth();
            if (BillType.INCOME.name().equals(bill.getType())) {
                incomeByDay[day] = incomeByDay[day].add(bill.getAmount());
            } else {
                expenseByDay[day] = expenseByDay[day].add(bill.getAmount());
            }
        }

        List<DailyVO.DailyPoint> days = new ArrayList<>();
        for (int i = 1; i <= daysInMonth; i++) {
            days.add(DailyVO.DailyPoint.builder()
                    .day(i)
                    .income(incomeByDay[i])
                    .expense(expenseByDay[i])
                    .build());
        }

        return DailyVO.builder().year(year).month(month).days(days).build();
    }
}
