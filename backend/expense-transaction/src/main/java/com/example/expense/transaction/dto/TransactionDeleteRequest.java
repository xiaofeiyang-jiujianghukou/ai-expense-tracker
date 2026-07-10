package com.example.expense.transaction.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class TransactionDeleteRequest {

    @NotNull(message = "ID不能为空")
    private Long id;
}
