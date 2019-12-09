package com.ita.if103java.ims.service.impl;

import com.ita.if103java.ims.dao.TransactionDao;
import com.ita.if103java.ims.dto.TransactionDto;
import com.ita.if103java.ims.entity.Transaction;
import com.ita.if103java.ims.entity.TransactionType;
import com.ita.if103java.ims.service.AccountService;
import com.ita.if103java.ims.service.AssociateService;
import com.ita.if103java.ims.service.ItemService;
import com.ita.if103java.ims.service.TransactionService;
import com.ita.if103java.ims.service.UserService;
import com.ita.if103java.ims.service.WarehouseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigInteger;

@Service
public class TransactionServiceImpl implements TransactionService {
    private TransactionDao transactionDao;
    private AccountService accountService;
    private UserService userService;
    private ItemService itemService;
    private WarehouseService warehouseService;
    private AssociateService associateService;

    @Autowired
    public TransactionServiceImpl(TransactionDao transactionDao, AccountService accountService,
                                  UserService userService, ItemService itemService,
                                  WarehouseService warehouseService, AssociateService associateService) {
        this.transactionDao = transactionDao;
        this.accountService = accountService;
        this.userService = userService;
        this.itemService = itemService;
        this.warehouseService = warehouseService;
        this.associateService = associateService;
    }

    @Override
    public TransactionDto findById(BigInteger id) {
        final Transaction transaction = transactionDao.findById(id);
        if (transaction.getType() == TransactionType.MOVE) {
            return new TransactionDto(
                transaction.getId(),
                transaction.getTimestamp(),
                transaction.getType(),
                accountService.view(transaction.getAccountId()),
                userService.findById(transaction.getWorkerId()),
                itemService.findById(transaction.getItemId()),
                transaction.getQuantity(),
                warehouseService.findWarehouseById(transaction.getMovedFrom()),
                warehouseService.findWarehouseById(transaction.getMovedTo())
            );
        }
        // TransactionType -> (IN, OUT)
        return new TransactionDto(
            transaction.getId(),
            transaction.getTimestamp(),
            transaction.getType(),
            accountService.view(transaction.getAccountId()),
            userService.findById(transaction.getWorkerId()),
            itemService.findById(transaction.getItemId()),
            transaction.getQuantity(),
            associateService.view(transaction.getAssociateId())
        );
    }
}
