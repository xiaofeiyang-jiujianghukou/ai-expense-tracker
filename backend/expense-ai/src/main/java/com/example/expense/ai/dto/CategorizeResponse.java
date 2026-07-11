package com.example.expense.ai.dto;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;

@Data
@Builder
public class CategorizeResponse {
    private Long categoryId;
    private String categoryName;
    private BigDecimal confidence;
    private String reason;
}
