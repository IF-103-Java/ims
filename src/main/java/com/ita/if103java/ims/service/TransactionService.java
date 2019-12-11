package com.ita.if103java.ims.service;

import com.ita.if103java.ims.dto.TransactionDto;

import java.math.BigInteger;

public interface TransactionService {
    TransactionDto findById(BigInteger id);
}
