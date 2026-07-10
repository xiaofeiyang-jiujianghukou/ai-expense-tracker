package com.example.expense.transaction.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@TableName("transaction")
public class Transaction {

    @TableId(type = IdType.AUTO)
    private Long id;
    private Long userId;
    private Long categoryId;
    private BigDecimal amount;
    private String type;       // INCOME / EXPENSE
    private String description;
    private LocalDate transactionDate;
    private LocalDateTime createdTime;
    private LocalDateTime updatedTime;
}
