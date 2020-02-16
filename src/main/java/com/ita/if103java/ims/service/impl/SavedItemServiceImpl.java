package com.ita.if103java.ims.service.impl;

import com.ita.if103java.ims.dao.AssociateDao;
import com.ita.if103java.ims.dao.ItemDao;
import com.ita.if103java.ims.dao.SavedItemDao;
import com.ita.if103java.ims.dao.WarehouseDao;
import com.ita.if103java.ims.dto.ItemDto;
import com.ita.if103java.ims.dto.ItemTransactionRequestDto;
import com.ita.if103java.ims.entity.AssociateType;
import com.ita.if103java.ims.entity.Item;
import com.ita.if103java.ims.entity.SavedItem;
import com.ita.if103java.ims.entity.TransactionType;
import com.ita.if103java.ims.exception.BaseRuntimeException;
import com.ita.if103java.ims.exception.service.SavedItemAddException;
import com.ita.if103java.ims.exception.service.SavedItemMoveException;
import com.ita.if103java.ims.exception.service.SavedItemOutException;
import com.ita.if103java.ims.exception.service.SavedItemValidateInputException;
import com.ita.if103java.ims.service.SavedItemService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class SavedItemServiceImpl implements SavedItemService {
    @Value("${items.maxWarehouseLoad}")
    private String maxWarehouseLoad;
    private ItemDao itemDao;
    private SavedItemDao savedItemDao;
    private WarehouseDao warehouseDao;
    private AssociateDao associateDao;

    @Autowired
    public SavedItemServiceImpl(ItemDao itemDao, SavedItemDao savedItemDao, WarehouseDao warehouseDao,
        AssociateDao associateDao) {
        this.itemDao = itemDao;
        this.savedItemDao = savedItemDao;
        this.warehouseDao = warehouseDao;
        this.associateDao = associateDao;
    }


    private void validateInputsAdd(ItemTransactionRequestDto itemTransaction, ItemDto itemDto, Long accountId) {
        if (isVolumeZeroOrLess(itemDto) ||
            warehouseDao.findById(itemTransaction.getDestinationWarehouseId(), accountId) == null ||
            associateDao.findById(accountId, itemTransaction.getAssociateId()).getType().equals(AssociateType.CLIENT) ||
            !(existInAccount(itemTransaction, accountId))) {
            throw new SavedItemAddException("Failed to get savedItem during `add`" + accountId + "}");
        }
    }

    private void validateInputsMove(ItemTransactionRequestDto itemTransaction, ItemDto itemDto, Long accountId) {
        if (isVolumeZeroOrLess(itemDto) ||
            !warehouseDao.findById(itemTransaction.getDestinationWarehouseId(), accountId).isBottom()
            || !(existInAccount(itemTransaction, accountId))) {
            throw new SavedItemMoveException("Failed to get savedItem during `move` {account_id = " + accountId + "}");
        }
    }

    private void validateInputsOut(ItemTransactionRequestDto itemTransaction, Long accountId) {
        if (associateDao.findById(accountId, itemTransaction.getAssociateId()).getType()
            .equals(AssociateType.SUPPLIER) ||
            !(itemDao.isExistItemById(itemTransaction.getItemId(), accountId))) {
            throw new SavedItemOutException("Failed to get savedItem during `outcomeItem` {account_id = " + accountId +
                " associateId = " + itemTransaction.getAssociateId() + "}");
        }
    }

    @Override
    public void validateInputs(ItemTransactionRequestDto itemTransaction, ItemDto itemDto, Long accountId,
        TransactionType type) {
        try {
            switch (type) {
                case IN -> validateInputsAdd(itemTransaction, itemDto, accountId);
                case MOVE -> validateInputsMove(itemTransaction, itemDto, accountId);
                case OUT -> validateInputsOut(itemTransaction, accountId);
            }
        } catch (BaseRuntimeException e) {
            throw new SavedItemValidateInputException(e.getMessage());
        }
    }

    @Override
    public boolean isEnoughCapacityInWarehouse(ItemTransactionRequestDto itemTransaction, ItemDto itemDto,
        Long accountId) {
        float volume =
            toVolumeOfPassSavedItems(itemTransaction.getDestinationWarehouseId(), accountId) +
                itemTransaction.getQuantity() * itemDto.getVolume();
        return warehouseDao.findById(itemTransaction.getDestinationWarehouseId(), accountId).getCapacity() >= volume;
    }

    @Override
    public float toVolumeOfPassSavedItems(Long warehouseId, Long accountId) {
        float volumePassSavedItems = 0;
        if (savedItemDao.existSavedItemByWarehouseId(warehouseId)) {
            List<SavedItem> savedItem = savedItemDao.findSavedItemByWarehouseId(warehouseId);
            if (savedItem.isEmpty()) {
                return 0;
            }
            String itemIds = savedItem.stream().map(x -> x.getItemId().toString()).collect(Collectors.joining(","));
            List<Item> item = itemDao.findItemsById(itemIds, accountId);
            for (int i = 0; i < savedItem.size(); i++) {
                volumePassSavedItems += savedItem.get(i).getQuantity() * item.get(i).getVolume();
            }
        }
        return volumePassSavedItems;
    }

    @Override
    public boolean isLowSpaceInWarehouse(ItemTransactionRequestDto itemTransaction, Long accountId) {
        float volume = toVolumeOfPassSavedItems(itemTransaction.getDestinationWarehouseId(), accountId);
        if (volume == 0) {
            return true;
        } else {
            return getCurrentWarehouseLoadPercentege(itemTransaction, volume, accountId) >
                Float.parseFloat(maxWarehouseLoad);
        }

    }

    @Override
    public boolean existInAccount(ItemTransactionRequestDto itemTransaction, Long accountId) {
        return itemDao.isExistItemById(itemTransaction.getItemId(), accountId) &&
            accountId
                .equals(warehouseDao.findById(itemTransaction.getDestinationWarehouseId(), accountId).getAccountID());
    }

    private boolean isVolumeZeroOrLess(ItemDto itemDto){
        return itemDto.getVolume() <= 0;
    }

    private float getCurrentWarehouseLoadPercentege(ItemTransactionRequestDto itemTransaction, float volume,
        Long accountId){
        return volume * 100 /
            warehouseDao.findById(itemTransaction.getDestinationWarehouseId(), accountId).getCapacity();
    }
}
