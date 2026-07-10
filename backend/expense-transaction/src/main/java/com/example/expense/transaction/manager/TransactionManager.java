package com.example.expense.transaction.manager;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.expense.category.entity.Category;
import com.example.expense.category.service.CategoryService;
import com.example.expense.common.exception.BusinessException;
import com.example.expense.transaction.dto.TransactionRequest;
import com.example.expense.transaction.dto.TransactionVO;
import com.example.expense.transaction.entity.Transaction;
import com.example.expense.transaction.service.TransactionService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.Map;
import java.util.stream.Collectors;

@Component("transactionBizManager")
@RequiredArgsConstructor
public class TransactionManager {

    private static final String UNKNOWN_CATEGORY = "未知";

    private String getCategoryName(Long categoryId) {
        try {
            return categoryService.findById(categoryId).getName();
        } catch (BusinessException e) {
            return UNKNOWN_CATEGORY;
        }
    }

    private final TransactionService transactionService;
    private final CategoryService categoryService;

    @Transactional
    public void create(TransactionRequest request, Long userId) {
        transactionService.create(request, userId);
    }

    public Page<TransactionVO> page(Long userId, String type, Long categoryId,
                                     LocalDate startDate, LocalDate endDate,
                                     int page, int size) {
        Page<Transaction> txnPage = transactionService.page(userId, type, categoryId,
                startDate, endDate, page, size);

        Map<Long, String> categoryNames = txnPage.getRecords().stream()
                .map(Transaction::getCategoryId)
                .distinct()
                .collect(Collectors.toMap(cid -> cid, this::getCategoryName));

        Page<TransactionVO> voPage = new Page<>(page, size, txnPage.getTotal());
        voPage.setRecords(txnPage.getRecords().stream()
                .map(t -> toVO(t, categoryNames.getOrDefault(t.getCategoryId(), "未知")))
                .toList());
        return voPage;
    }

    public TransactionVO getById(Long id, Long userId) {
        Transaction txn = transactionService.findById(id, userId);
        return toVO(txn, getCategoryName(txn.getCategoryId()));
    }

    @Transactional
    public void update(Long id, TransactionRequest request, Long userId) {
        transactionService.update(id, request, userId);
    }

    @Transactional
    public void delete(Long id, Long userId) {
        transactionService.delete(id, userId);
    }

    private TransactionVO toVO(Transaction txn, String categoryName) {
        return TransactionVO.builder()
                .id(txn.getId())
                .categoryId(txn.getCategoryId())
                .categoryName(categoryName)
                .amount(txn.getAmount())
                .type(txn.getType())
                .description(txn.getDescription())
                .transactionDate(txn.getTransactionDate())
                .createdTime(txn.getCreatedTime())
                .updatedTime(txn.getUpdatedTime())
                .build();
    }
}
