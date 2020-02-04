package com.ita.if103java.ims.service;

import com.ita.if103java.ims.dto.ItemTransactionRequestDto;
import com.ita.if103java.ims.security.UserDetailsImpl;


public interface SavedItemService {
    void validateInputsAdd(ItemTransactionRequestDto itemTransaction, Long accountId);

    void validateInputsMove(ItemTransactionRequestDto itemTransaction, Long accountId);

    void validateInputsOut(ItemTransactionRequestDto itemTransaction, UserDetailsImpl user);

    boolean isEnoughCapacityInWarehouse(ItemTransactionRequestDto itemTransaction, Long accountId);

    float toVolumeOfPassSavedItems(ItemTransactionRequestDto itemTransaction, Long accountId);

    float toVolumeOfPassSavedItems(Long warehouseId, Long accountId);

    boolean isLowSpaceInWarehouse(ItemTransactionRequestDto itemTransaction, Long accountId);

    boolean existInAccount(ItemTransactionRequestDto itemTransaction, Long accountId);
}
