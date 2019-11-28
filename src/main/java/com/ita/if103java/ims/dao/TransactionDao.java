package com.ita.if103java.ims.dao;

import com.ita.if103java.ims.entity.Transaction;
import org.springframework.dao.DataAccessException;

import java.math.BigInteger;
import java.util.List;
import java.util.Map;

public interface TransactionDao {
    Transaction create(Transaction transaction) throws DataAccessException;

    Transaction findById(BigInteger id) throws DataAccessException;

    List<Transaction> findAll(Integer offset, Integer limit) throws DataAccessException;

    List<Transaction> findAll(Map<String, Object> params,
                              Integer offset, Integer limit,
                              String orderBy) throws DataAccessException, IllegalStateException;
}
