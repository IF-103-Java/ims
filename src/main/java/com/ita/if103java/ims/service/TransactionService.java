package com.ita.if103java.ims.service;

import com.ita.if103java.ims.dto.ItemTransactionRequestDto;
import com.ita.if103java.ims.dto.TransactionDto;
import com.ita.if103java.ims.entity.Transaction;
import com.ita.if103java.ims.entity.TransactionType;
import com.ita.if103java.ims.entity.User;
import com.ita.if103java.ims.security.UserDetailsImpl;

public interface TransactionService {
    TransactionDto findById(Long id, UserDetailsImpl userDetails);

    Transaction create(ItemTransactionRequestDto itemTransactionRequestDto,
                       User user,
                       Long associateId,
                       TransactionType type);
}
