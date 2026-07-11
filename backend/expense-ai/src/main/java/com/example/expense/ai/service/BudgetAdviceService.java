package com.example.expense.ai.service;

import com.example.expense.ai.client.LlmClient;
import com.example.expense.ai.dto.AnalysisRequest;
import com.example.expense.ai.dto.AnalysisResponse;
import com.example.expense.bill.entity.Bill;
import com.example.expense.category.entity.Category;
import com.example.expense.category.service.CategoryService;
import com.example.expense.common.enums.BillType;
import com.example.expense.common.exception.BusinessException;
import com.example.expense.common.exception.ErrorCode;
import com.example.expense.statistics.service.StatisticsService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class BudgetAdviceService {

    private final StatisticsService statisticsService;
    private final CategoryService categoryService;
    private final LlmClient llmClient;

    public AnalysisResponse generate(AnalysisRequest request, Long userId) {
        int year = request.getYear();
        int month = request.getMonth();

        // 1. Get all expense categories
        List<Category> categories = categoryService.listByUser(userId, BillType.EXPENSE);
        Map<Long, String> catNames = categories.stream()
                .collect(Collectors.toMap(Category::getId, Category::getName));

        // 2. Query bills for last 3 months
        LocalDate today = LocalDate.now();
        LocalDate threeMonthsAgo = today.minusMonths(3).withDayOfMonth(1);
        List<Bill> allBills = statisticsService.queryByDateRange(userId, threeMonthsAgo, today);

        // Filter to expense only
        List<Bill> expenseBills = allBills.stream()
                .filter(b -> BillType.EXPENSE.name().equals(b.getType()))
                .toList();

        // 3. Build per-category monthly data
        Map<Long, Map<String, BigDecimal>> catMonthlyData = new LinkedHashMap<>(); // catId -> {"2026-5": sum}
        for (Bill b : expenseBills) {
            String key = b.getBillDate().getYear() + "-" + b.getBillDate().getMonthValue();
            catMonthlyData.computeIfAbsent(b.getCategoryId(), k -> new LinkedHashMap<>())
                    .merge(key, b.getAmount(), BigDecimal::add);
        }

        // 4. Find first recording day in current month
        LocalDate monthStart = LocalDate.of(year, month, 1);
        LocalDate monthEnd = monthStart.plusMonths(1).minusDays(1);
        LocalDate searchEnd = today.isBefore(monthEnd) ? today : monthEnd;

        Optional<LocalDate> firstDayOpt = expenseBills.stream()
                .filter(b -> b.getBillDate().getYear() == year && b.getBillDate().getMonthValue() == month)
                .map(Bill::getBillDate)
                .min(LocalDate::compareTo);

        int daysInMonth = monthStart.lengthOfMonth();
        long recordedDays = firstDayOpt
                .map(first -> ChronoUnit.DAYS.between(first, searchEnd) + 1)
                .orElse(0L);

        // 5. Build context for each category
        StringBuilder context = new StringBuilder();
        int validCatCount = 0;
        for (Category cat : categories) {
            Map<String, BigDecimal> monthly = catMonthlyData.getOrDefault(cat.getId(), Map.of());
            if (monthly.isEmpty()) continue;
            validCatCount++;

            // Get last 3 months data
            List<BigDecimal> monthValues = new ArrayList<>();
            List<String> monthLabels = new ArrayList<>();
            LocalDate cursor = threeMonthsAgo;
            while (!cursor.isAfter(searchEnd)) {
                String key = cursor.getYear() + "-" + cursor.getMonthValue();
                BigDecimal val = monthly.getOrDefault(key, BigDecimal.ZERO);
                monthValues.add(val);
                monthLabels.add(cursor.getYear() + "年" + cursor.getMonthValue() + "月");
                cursor = cursor.plusMonths(1);
            }

            // Calculate stats
            List<BigDecimal> validValues = monthValues.stream()
                    .filter(v -> v.compareTo(BigDecimal.ZERO) > 0).toList();
            BigDecimal avg3Month = validValues.isEmpty() ? BigDecimal.ZERO
                    : validValues.stream().reduce(BigDecimal.ZERO, BigDecimal::add)
                    .divide(BigDecimal.valueOf(validValues.size()), 2, RoundingMode.HALF_UP);

            // Trend: compare last 2 valid months
            String trend = "→";
            if (validValues.size() >= 2) {
                BigDecimal latest = validValues.get(validValues.size() - 1);
                BigDecimal prev = validValues.get(validValues.size() - 2);
                int cmp = latest.compareTo(prev);
                trend = cmp > 0 ? "↑增长" : cmp < 0 ? "↓下降" : "→持平";
            }

            // Current month: daily rate and projected full month
            String currentMonthKey = year + "-" + month;
            BigDecimal currentMonthSum = monthly.getOrDefault(currentMonthKey, BigDecimal.ZERO);
            BigDecimal dailyAvg = recordedDays > 0
                    ? currentMonthSum.divide(BigDecimal.valueOf(recordedDays), 2, RoundingMode.HALF_UP)
                    : BigDecimal.ZERO;
            BigDecimal projectedFull = dailyAvg.multiply(BigDecimal.valueOf(daysInMonth)).setScale(0, RoundingMode.HALF_UP);

            context.append(String.format("""
                    **%s**:
                    - 近3月: %s
                    - 近3月有效月均值: ¥%s
                    - 趋势: %s
                    - 本月(已记录%d天，日均¥%s，推算全月¥%s)

                    """,
                    cat.getName(),
                    buildMonthDetail(monthLabels, monthValues),
                    avg3Month.toPlainString(),
                    trend,
                    recordedDays, dailyAvg.toPlainString(), projectedFull.toPlainString()));
        }

        if (validCatCount == 0) {
            return AnalysisResponse.builder().year(year).month(month)
                    .insights(List.of("暂无足够消费数据用于预算分析，建议先记录1-2个月后再试。")).build();
        }

        // 6. Build prompt
        String systemPrompt = "你是一位专业的个人理财顾问。" +
                "你需要根据以下数据分析每个分类的消费模式，给出合理的月度预算建议。\n\n" +
                "数据说明：\n" +
                "- \"有效月\"指该分类有实际消费的月份，¥0表示该月未记录消费\n" +
                "- \"近3月有效月均值\"排除了消费为0的月份\n" +
                "- \"本月推算全月\"根据本月实际日均支出推算，仅供参考\n" +
                "- \"趋势\"为该分类近两个有效月的变动方向\n\n" +
                "预算建议规则：\n" +
                "- 优先参考近3月有效月均值，趋势上行适当上浮10-20%，趋势下行参考均值即可\n" +
                "- 本月为推算值时降低权重，优先参考历史有效月数据\n" +
                "- 金额取整数，注意避开250\n" +
                "- 如果某分类仅有1个月数据或波动很大，说明原因并给出保守建议\n\n" +
                "输出格式：分类名: ¥金额 — 理由";

        String userMessage = String.format("""
                用户%d年%d月预算分析数据，本月已记录%d天（共%d天）：

                %s
                请为每个有数据的分类建议合理的月度预算金额。""",
                year, month, recordedDays, daysInMonth, context.toString());

        // 7. Call LLM
        try {
            String response = llmClient.chat(systemPrompt, userMessage);
            return AnalysisResponse.builder()
                    .year(year).month(month)
                    .insights(parseLines(response))
                    .build();
        } catch (Exception e) {
            throw new BusinessException(ErrorCode.AI_ANALYSIS_FAILED);
        }
    }

    private String buildMonthDetail(List<String> labels, List<BigDecimal> values) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < labels.size(); i++) {
            if (i > 0) sb.append(", ");
            BigDecimal v = values.get(i);
            if (v.compareTo(BigDecimal.ZERO) > 0) {
                sb.append(String.format("%s ¥%s(有效)", labels.get(i), v.toPlainString()));
            } else {
                sb.append(String.format("%s ¥0(未记录)", labels.get(i)));
            }
        }
        return sb.toString();
    }

    private List<String> parseLines(String llmResponse) {
        List<String> result = new ArrayList<>();
        for (String line : llmResponse.split("\n")) {
            String trimmed = line.trim();
            trimmed = trimmed.replaceFirst("^\\d+[.、．)\\s]+", "").trim();
            if (!trimmed.isEmpty()) {
                result.add(trimmed);
            }
        }
        if (result.isEmpty()) result.add(llmResponse.trim());
        return result;
    }
}
