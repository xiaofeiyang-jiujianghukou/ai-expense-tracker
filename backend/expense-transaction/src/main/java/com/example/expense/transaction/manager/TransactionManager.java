package com.example.expense.transaction.manager;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.expense.category.entity.Category;
import com.example.expense.category.service.CategoryService;
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

@Component
@RequiredArgsConstructor
public class TransactionManager {

    private final TransactionService transactionService;
    private final CategoryService categoryService;

    @Transactional
    public TransactionVO create(TransactionRequest request, Long userId) {
        Transaction txn = transactionService.create(request, userId);
        Category category = categoryService.findById(txn.getCategoryId());
        return toVO(txn, category.getName());
    }

    public Page<TransactionVO> page(Long userId, String type, Long categoryId,
                                     LocalDate startDate, LocalDate endDate,
                                     int page, int size) {
        Page<Transaction> txnPage = transactionService.page(userId, type, categoryId,
                startDate, endDate, page, size);

        // Batch load category names
        Map<Long, String> categoryNames = txnPage.getRecords().stream()
                .map(Transaction::getCategoryId)
                .distinct()
                .map(categoryService::findById)
                .collect(Collectors.toMap(Category::getId, Category::getName));

        Page<TransactionVO> voPage = new Page<>(page, size, txnPage.getTotal());
        voPage.setRecords(txnPage.getRecords().stream()
                .map(t -> toVO(t, categoryNames.getOrDefault(t.getCategoryId(), "未知")))
                .toList());
        return voPage;
    }

    public TransactionVO getById(Long id, Long userId) {
        Transaction txn = transactionService.findById(id, userId);
        Category category = categoryService.findById(txn.getCategoryId());
        return toVO(txn, category.getName());
    }

    @Transactional
    public TransactionVO update(Long id, TransactionRequest request, Long userId) {
        Transaction txn = transactionService.update(id, request, userId);
        Category category = categoryService.findById(txn.getCategoryId());
        return toVO(txn, category.getName());
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
