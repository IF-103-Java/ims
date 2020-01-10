package com.ita.if103java.ims.service;

import com.ita.if103java.ims.dto.TransactionDto;
import com.ita.if103java.ims.security.UserDetailsImpl;

public interface TransactionService {
    TransactionDto findById(Long id, UserDetailsImpl userDetails);
}
