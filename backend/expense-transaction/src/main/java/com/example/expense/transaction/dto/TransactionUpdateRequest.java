package com.example.expense.transaction.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class TransactionUpdateRequest extends TransactionRequest {

    @NotNull(message = "ID不能为空")
    private Long id;
}
