package com.ita.if103java.ims.service.impl;

import com.ita.if103java.ims.dao.AssociateDao;
import com.ita.if103java.ims.dao.ItemDao;
import com.ita.if103java.ims.dao.SavedItemDao;
import com.ita.if103java.ims.dao.WarehouseDao;
import com.ita.if103java.ims.dto.ItemTransactionRequestDto;
import com.ita.if103java.ims.entity.AssociateType;
import com.ita.if103java.ims.entity.Item;
import com.ita.if103java.ims.entity.SavedItem;
import com.ita.if103java.ims.exception.dao.SavedItemNotFoundException;
import com.ita.if103java.ims.security.UserDetailsImpl;
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
    public SavedItemServiceImpl(ItemDao itemDao, SavedItemDao savedItemDao, WarehouseDao warehouseDao, AssociateDao associateDao) {
        this.itemDao = itemDao;
        this.savedItemDao = savedItemDao;
        this.warehouseDao = warehouseDao;
        this.associateDao = associateDao;
    }
    @Override
    public void validateInputsAdd(ItemTransactionRequestDto itemTransaction, Long accountId) {
        if (itemTransaction.getItemDto().getVolume()<=0 && associateDao.findById(accountId, itemTransaction.getAssociateId()).getType().equals(AssociateType.CLIENT) &&
             !(existInAccount(itemTransaction, accountId) &&
            associateDao.findById(accountId, itemTransaction.getAssociateId()).getAccountId().equals(accountId))) {
            throw new SavedItemNotFoundException("Failed to get savedItem during `create` {account_id = " + itemTransaction.getItemDto().getAccountId() + "}");
        }
    }
    @Override
    public void validateInputsMove(ItemTransactionRequestDto itemTransaction, Long accountId) {
        if (!warehouseDao.findById(itemTransaction.getDestinationWarehouseId(), accountId).isBottom() && !(existInAccount(itemTransaction, accountId))) {
            throw new SavedItemNotFoundException("Failed to get savedItem during `move` {account_id = " + itemTransaction.getItemDto().getAccountId() + "}");
        }
    }
    @Override
    public void validateInputsOut(ItemTransactionRequestDto itemTransaction, UserDetailsImpl user) {
        Long accountId = user.getUser().getAccountId();
        if (associateDao.findById(accountId, itemTransaction.getAssociateId()).getType().equals(AssociateType.SUPPLIER) && !(itemDao.isExistItemById(itemTransaction.getItemDto().getId(), user.getUser().getAccountId())
            && associateDao.findById(accountId, itemTransaction.getAssociateId()).getAccountId().equals(accountId))) {
            throw new SavedItemNotFoundException("Failed to get savedItem during `outcomeItem` {account_id = " + accountId +
                " associateId = " + itemTransaction.getAssociateId() + "}");
        }
    }
    @Override
    public boolean isEnoughCapacityInWarehouse(ItemTransactionRequestDto itemTransaction, Long accountId) {
        float volume =
            toVolumeOfPassSavedItems(itemTransaction, accountId) + itemTransaction.getQuantity() * itemTransaction.getItemDto().getVolume();
        return warehouseDao.findById(itemTransaction.getDestinationWarehouseId(), accountId).getCapacity() >= volume;
    }

    @Override
    public float toVolumeOfPassSavedItems(ItemTransactionRequestDto itemTransaction, Long accountId) {
        float volumePassSavedItems = 0;
        Long warehouseId = itemTransaction.getDestinationWarehouseId();

        if (savedItemDao.existSavedItemByWarehouseId(warehouseId)) {
            List<SavedItem> savedItem =  savedItemDao.findSavedItemByWarehouseId(warehouseId);
            String itemIds = savedItem.stream().map(x->x.getItemId().toString()).collect(Collectors.joining(","));
            List<Item> item = itemDao.findItemsById(itemIds, accountId);
            for (int i = 0; i < savedItem.size(); i++) {
                volumePassSavedItems += savedItem.get(i).getQuantity() * item.get(i).getVolume();
            }
        }
        return volumePassSavedItems;
    }
    @Override
    public float toVolumeOfPassSavedItems(Long warehouseId, Long accountId) {
        float volumePassSavedItems = 0;

        if (savedItemDao.existSavedItemByWarehouseId(warehouseId)) {
            for (SavedItem savedItem : savedItemDao.findSavedItemByWarehouseId(warehouseId)) {
                Item item = itemDao.findItemById(savedItem.getItemId(), accountId);
                volumePassSavedItems += savedItem.getQuantity() * item.getVolume();
            }
        }
        return volumePassSavedItems;
    }
    @Override
    public boolean isLowSpaceInWarehouse(ItemTransactionRequestDto itemTransaction, Long accountId) {
        float volume = toVolumeOfPassSavedItems(itemTransaction, accountId);
        if (volume == 0) {
            return true;
        } else {
            return volume * 100 / warehouseDao.findById(itemTransaction.getDestinationWarehouseId(), accountId).getCapacity() > Float.parseFloat(maxWarehouseLoad);
        }

    }
    @Override
    public boolean existInAccount(ItemTransactionRequestDto itemTransaction, Long accountId) {
        return itemDao.isExistItemById(itemTransaction.getItemDto().getId(), accountId) &&
            accountId.equals(warehouseDao.findById(itemTransaction.getDestinationWarehouseId(), accountId).getAccountID());
    }
}
