package com.example.expense.transaction.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.expense.category.entity.Category;
import com.example.expense.category.service.CategoryService;
import com.example.expense.common.exception.BusinessException;
import com.example.expense.common.exception.ErrorCode;
import com.example.expense.transaction.dto.TransactionRequest;
import com.example.expense.transaction.dto.TransactionVO;
import com.example.expense.transaction.entity.Transaction;
import com.example.expense.transaction.mapper.TransactionMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class TransactionService {

    private final TransactionMapper transactionMapper;
    private final CategoryService categoryService;

    // ── 写操作 ──

    public void create(TransactionRequest request, Long userId) {
        Transaction txn = new Transaction();
        txn.setUserId(userId);
        txn.setCategoryId(request.getCategoryId());
        txn.setAmount(request.getAmount());
        txn.setType(request.getType());
        txn.setDescription(request.getDescription());
        txn.setTransactionDate(request.getTransactionDate());
        transactionMapper.insert(txn);
    }

    public void update(Long id, TransactionRequest request, Long userId) {
        Transaction txn = findById(id, userId);
        txn.setCategoryId(request.getCategoryId());
        txn.setAmount(request.getAmount());
        txn.setType(request.getType());
        txn.setDescription(request.getDescription());
        txn.setTransactionDate(request.getTransactionDate());
        transactionMapper.updateById(txn);
    }

    public void delete(Long id, Long userId) {
        findById(id, userId);
        transactionMapper.deleteById(id);
    }

    // ── 读操作（含分类名） ──

    public Page<TransactionVO> page(Long userId, String type, Long categoryId,
                                     LocalDate startDate, LocalDate endDate,
                                     int page, int size) {
        LambdaQueryWrapper<Transaction> wrapper = new LambdaQueryWrapper<Transaction>()
                .eq(Transaction::getUserId, userId);
        if (type != null && !type.isBlank()) wrapper.eq(Transaction::getType, type);
        if (categoryId != null) wrapper.eq(Transaction::getCategoryId, categoryId);
        if (startDate != null) wrapper.ge(Transaction::getTransactionDate, startDate);
        if (endDate != null) wrapper.le(Transaction::getTransactionDate, endDate);
        wrapper.orderByDesc(Transaction::getTransactionDate)
               .orderByDesc(Transaction::getCreatedTime);

        Page<Transaction> txnPage = transactionMapper.selectPage(Page.of(page, size), wrapper);

        Map<Long, String> catNames = txnPage.getRecords().stream()
                .map(Transaction::getCategoryId)
                .distinct()
                .collect(Collectors.toMap(cid -> cid, this::getCategoryName));

        Page<TransactionVO> voPage = new Page<>(page, size, txnPage.getTotal());
        voPage.setRecords(txnPage.getRecords().stream()
                .map(t -> toVO(t, catNames.getOrDefault(t.getCategoryId(), "未知")))
                .toList());
        return voPage;
    }

    public TransactionVO getById(Long id, Long userId) {
        Transaction txn = findById(id, userId);
        return toVO(txn, getCategoryName(txn.getCategoryId()));
    }

    // ── 内部 ──

    public Transaction findById(Long id, Long userId) {
        Transaction txn = transactionMapper.selectById(id);
        if (txn == null) throw new BusinessException(ErrorCode.TRANSACTION_NOT_FOUND);
        if (!txn.getUserId().equals(userId)) throw new BusinessException(ErrorCode.FORBIDDEN);
        return txn;
    }

    private String getCategoryName(Long categoryId) {
        try {
            return categoryService.findById(categoryId).getName();
        } catch (BusinessException e) {
            return "未知";
        }
    }

    private TransactionVO toVO(Transaction txn, String categoryName) {
        return TransactionVO.builder()
                .id(txn.getId()).categoryId(txn.getCategoryId()).categoryName(categoryName)
                .amount(txn.getAmount()).type(txn.getType()).description(txn.getDescription())
                .transactionDate(txn.getTransactionDate())
                .createdTime(txn.getCreatedTime()).updatedTime(txn.getUpdatedTime())
                .build();
    }
}
