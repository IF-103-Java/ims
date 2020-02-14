package com.ita.if103java.ims.service;

import com.ita.if103java.ims.dto.ItemDto;
import com.ita.if103java.ims.dto.ItemTransactionRequestDto;
import com.ita.if103java.ims.entity.TransactionType;


public interface SavedItemService {
    void validateInputs(ItemTransactionRequestDto itemTransaction, ItemDto itemDto, Long accountId,
        TransactionType type);

    boolean isEnoughCapacityInWarehouse(ItemTransactionRequestDto itemTransaction, ItemDto itemDto,
        Long accountId);

    float toVolumeOfPassSavedItems(Long warehouseId, Long accountId);

    boolean isLowSpaceInWarehouse(ItemTransactionRequestDto itemTransaction, Long accountId);

    boolean existInAccount(ItemTransactionRequestDto itemTransaction, Long accountId);

}
