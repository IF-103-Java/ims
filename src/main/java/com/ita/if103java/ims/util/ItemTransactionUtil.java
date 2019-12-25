package com.ita.if103java.ims.util;

import com.ita.if103java.ims.dto.ItemTransactionRequestDto;
import com.ita.if103java.ims.entity.Transaction;
import com.ita.if103java.ims.entity.TransactionType;
import com.ita.if103java.ims.entity.User;

public class ItemTransactionUtil {

    public static Transaction createTransaction(ItemTransactionRequestDto itemTransaction, User user, Long associateId, TransactionType type) {
        Transaction transaction = new Transaction();
        transaction.setAccountId(user.getAccountId());
        transaction.setAssociateId(associateId);
        transaction.setItemId(itemTransaction.getItemDto().getId());
        transaction.setQuantity(itemTransaction.getQuantity());
        transaction.setWorkerId(user.getId());
        transaction.setType(type);
        switch (type) {
            case OUT -> transaction.setMovedTo(associateId);
            case IN -> transaction.setMovedFrom(associateId);
        }
        return transaction;
    }
}
