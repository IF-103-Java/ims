package com.ita.if103java.ims.util;

import com.ita.if103java.ims.dto.SavedItemDto;
import com.ita.if103java.ims.entity.Associate;
import com.ita.if103java.ims.entity.Transaction;
import com.ita.if103java.ims.entity.TransactionType;
import com.ita.if103java.ims.entity.User;

public class ItemTransactionUtil {

    public static Transaction createTransaction(SavedItemDto savedItemDto, User user, Associate associate, TransactionType type) {
        Transaction transaction = new Transaction();
        transaction.setAccountId(user.getAccountId());
        transaction.setAssociateId(associate.getId());
        transaction.setItemId(savedItemDto.getItemId());
        transaction.setQuantity(Long.getLong(savedItemDto.getQuantity() + ""));
        transaction.setWorkerId(user.getId());
        transaction.setType(type);
        switch (type) {
            case OUT -> transaction.setMovedTo(associate.getId());
            case IN -> transaction.setMovedFrom(associate.getId());
        }
        return transaction;
    }
}
