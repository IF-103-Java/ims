package com.ita.if103java.ims.dao;

import com.ita.if103java.ims.entity.Transaction;

import java.util.List;

public interface TransactionDao {
    Transaction create(Transaction transaction);

    Transaction findById(Long id);

    List<Transaction> findAll();

}
