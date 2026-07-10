package com.example.expense.transaction.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.expense.transaction.entity.Transaction;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface TransactionMapper extends BaseMapper<Transaction> {
}
