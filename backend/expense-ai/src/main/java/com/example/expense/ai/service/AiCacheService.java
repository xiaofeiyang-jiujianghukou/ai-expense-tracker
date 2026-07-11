package com.example.expense.ai.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;

@Service
@RequiredArgsConstructor
public class AiCacheService {

    private static final String ANALYSIS_PREFIX = "ai:analysis:";
    private static final String REPORT_PREFIX = "ai:report:";
    private static final Duration ANALYSIS_TTL = Duration.ofHours(1);
    private static final Duration REPORT_TTL = Duration.ofHours(6);

    private final StringRedisTemplate redisTemplate;

    public String getAnalysis(Long userId, int year, int month) {
        return redisTemplate.opsForValue().get(analysisKey(userId, year, month));
    }

    public void putAnalysis(Long userId, int year, int month, String value) {
        redisTemplate.opsForValue().set(analysisKey(userId, year, month), value, ANALYSIS_TTL);
    }

    public String getReport(Long userId, int year, int month) {
        return redisTemplate.opsForValue().get(reportKey(userId, year, month));
    }

    public void putReport(Long userId, int year, int month, String value) {
        redisTemplate.opsForValue().set(reportKey(userId, year, month), value, REPORT_TTL);
    }

    private static String analysisKey(Long userId, int year, int month) {
        return ANALYSIS_PREFIX + userId + ":" + year + ":" + month;
    }

    private static String reportKey(Long userId, int year, int month) {
        return REPORT_PREFIX + userId + ":" + year + ":" + month;
    }
}
