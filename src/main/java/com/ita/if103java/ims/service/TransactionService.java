package com.ita.if103java.ims.service;

import com.ita.if103java.ims.dto.TransactionDto;

public interface TransactionService {
    TransactionDto findById(Long id);
}
