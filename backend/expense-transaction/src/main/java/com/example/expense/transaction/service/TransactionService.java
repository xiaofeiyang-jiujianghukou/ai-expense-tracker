package com.example.expense.transaction.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.expense.common.exception.BusinessException;
import com.example.expense.common.exception.ErrorCode;
import com.example.expense.transaction.dto.TransactionRequest;
import com.example.expense.transaction.entity.Transaction;
import com.example.expense.transaction.mapper.TransactionMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class TransactionService {

    private final TransactionMapper transactionMapper;

    public Transaction create(TransactionRequest request, Long userId) {
        Transaction txn = new Transaction();
        txn.setUserId(userId);
        txn.setCategoryId(request.getCategoryId());
        txn.setAmount(request.getAmount());
        txn.setType(request.getType());
        txn.setDescription(request.getDescription());
        txn.setTransactionDate(request.getTransactionDate());
        txn.setCreatedTime(LocalDateTime.now());
        txn.setUpdatedTime(LocalDateTime.now());
        transactionMapper.insert(txn);
        return txn;
    }

    public Page<Transaction> page(Long userId, String type, Long categoryId,
                                   LocalDate startDate, LocalDate endDate,
                                   int page, int size) {
        LambdaQueryWrapper<Transaction> wrapper = new LambdaQueryWrapper<Transaction>()
                .eq(Transaction::getUserId, userId);

        if (type != null && !type.isBlank()) {
            wrapper.eq(Transaction::getType, type);
        }
        if (categoryId != null) {
            wrapper.eq(Transaction::getCategoryId, categoryId);
        }
        if (startDate != null) {
            wrapper.ge(Transaction::getTransactionDate, startDate);
        }
        if (endDate != null) {
            wrapper.le(Transaction::getTransactionDate, endDate);
        }
        wrapper.orderByDesc(Transaction::getTransactionDate)
               .orderByDesc(Transaction::getCreatedTime);

        return transactionMapper.selectPage(Page.of(page, size), wrapper);
    }

    public Transaction findById(Long id, Long userId) {
        Transaction txn = transactionMapper.selectById(id);
        if (txn == null) {
            throw new BusinessException(ErrorCode.TRANSACTION_NOT_FOUND);
        }
        if (!txn.getUserId().equals(userId)) {
            throw new BusinessException(ErrorCode.FORBIDDEN);
        }
        return txn;
    }

    public Transaction update(Long id, TransactionRequest request, Long userId) {
        Transaction txn = findById(id, userId);
        txn.setCategoryId(request.getCategoryId());
        txn.setAmount(request.getAmount());
        txn.setType(request.getType());
        txn.setDescription(request.getDescription());
        txn.setTransactionDate(request.getTransactionDate());
        txn.setUpdatedTime(LocalDateTime.now());
        transactionMapper.updateById(txn);
        return txn;
    }

    public void delete(Long id, Long userId) {
        findById(id, userId);  // verify ownership
        transactionMapper.deleteById(id);
    }
}
