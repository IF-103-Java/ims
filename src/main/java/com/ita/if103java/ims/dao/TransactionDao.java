package com.ita.if103java.ims.dao;

import com.ita.if103java.ims.entity.Transaction;
import org.springframework.dao.DataAccessException;

import java.math.BigInteger;
import java.util.List;
import java.util.Map;

public interface TransactionDao {
    Transaction create(Transaction transaction);

    Transaction findById(BigInteger id);

    List<Transaction> findAll(Integer offset, Integer limit);

    List<Transaction> findAll(Map<String, Object> params,
                              Integer offset, Integer limit,
                              String orderBy);
}
