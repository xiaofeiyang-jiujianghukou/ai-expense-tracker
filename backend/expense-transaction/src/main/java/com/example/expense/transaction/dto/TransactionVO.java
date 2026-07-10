package com.example.expense.transaction.dto;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@Builder
public class TransactionVO {
    private Long id;
    private Long categoryId;
    private String categoryName;
    private BigDecimal amount;
    private String type;
    private String description;
    private LocalDate transactionDate;
    private LocalDateTime createdTime;
    private LocalDateTime updatedTime;
}
