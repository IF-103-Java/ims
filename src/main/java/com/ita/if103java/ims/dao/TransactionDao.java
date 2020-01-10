package com.ita.if103java.ims.dao;

import com.ita.if103java.ims.dto.ItemTransactionRequestDto;
import com.ita.if103java.ims.entity.Transaction;
import com.ita.if103java.ims.entity.TransactionType;
import com.ita.if103java.ims.entity.User;

public interface TransactionDao {
    Transaction create(Transaction transaction);

    Transaction create(ItemTransactionRequestDto itemTransactionRequestDto,
                       User user,
                       Long associateId,
                       TransactionType type);

    Transaction findById(Long id);
}
