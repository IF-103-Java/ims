package com.ita.if103java.ims.service.impl;

import com.ita.if103java.ims.dao.TransactionDao;
import com.ita.if103java.ims.dto.ItemTransactionRequestDto;
import com.ita.if103java.ims.dto.TransactionDto;
import com.ita.if103java.ims.entity.Transaction;
import com.ita.if103java.ims.entity.TransactionType;
import com.ita.if103java.ims.entity.User;
import com.ita.if103java.ims.security.UserDetailsImpl;
import com.ita.if103java.ims.service.AccountService;
import com.ita.if103java.ims.service.AssociateService;
import com.ita.if103java.ims.service.ItemService;
import com.ita.if103java.ims.service.TransactionService;
import com.ita.if103java.ims.service.UserService;
import com.ita.if103java.ims.service.WarehouseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

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
    public TransactionDto findById(Long id, UserDetailsImpl userDetails) {
        final Transaction transaction = transactionDao.findById(id);
        return switch (transaction.getType()) {
            case IN -> buildIncomeTransactionDto(transaction, userDetails);
            case OUT -> buildOutcomeTransactionDto(transaction, userDetails);
            case MOVE -> buildMoveTransactionDto(transaction, userDetails);
        };
    }

    @Override
    public Transaction create(ItemTransactionRequestDto itemTransactionRequestDto,
                              User user,
                              Long associateId,
                              TransactionType type) {
        final Transaction transaction = new Transaction();
        transaction.setAccountId(user.getAccountId());
        transaction.setAssociateId(associateId);
        transaction.setItemId(itemTransactionRequestDto.getItemDto().getId());
        transaction.setQuantity(itemTransactionRequestDto.getQuantity());
        transaction.setWorkerId(user.getId());
        transaction.setType(type);
        switch (type) {
            case OUT -> transaction.setMovedFrom(itemTransactionRequestDto.getSourceWarehouseId());
            case IN -> transaction.setMovedTo(itemTransactionRequestDto.getDestinationWarehouseId());
        }
        return transactionDao.create(transaction);
    }

    private TransactionDto buildIncomeTransactionDto(Transaction transaction, UserDetailsImpl userDetails) {
        return new TransactionDto(
            transaction.getId(),
            transaction.getTimestamp(),
            transaction.getType(),
            accountService.view(transaction.getAccountId()),
            userService.findById(transaction.getWorkerId()),
            itemService.findById(transaction.getItemId(), userDetails),
            transaction.getQuantity(),
            associateService.view(transaction.getAssociateId()),
            null,
            warehouseService.findWarehouseById(transaction.getMovedTo())
        );
    }

    private TransactionDto buildOutcomeTransactionDto(Transaction transaction, UserDetailsImpl userDetails) {
        return new TransactionDto(
            transaction.getId(),
            transaction.getTimestamp(),
            transaction.getType(),
            accountService.view(transaction.getAccountId()),
            userService.findById(transaction.getWorkerId()),
            itemService.findById(transaction.getItemId(), userDetails),
            transaction.getQuantity(),
            associateService.view(transaction.getAssociateId()),
            warehouseService.findWarehouseById(transaction.getMovedFrom()),
            null
        );
    }

    private TransactionDto buildMoveTransactionDto(Transaction transaction, UserDetailsImpl userDetails) {
        return new TransactionDto(
            transaction.getId(),
            transaction.getTimestamp(),
            transaction.getType(),
            accountService.view(transaction.getAccountId()),
            userService.findById(transaction.getWorkerId()),
            itemService.findById(transaction.getItemId(), userDetails),
            transaction.getQuantity(),
            null,
            warehouseService.findWarehouseById(transaction.getMovedFrom()),
            warehouseService.findWarehouseById(transaction.getMovedTo())
        );
    }
}
