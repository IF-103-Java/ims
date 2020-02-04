package com.ita.if103java.ims.service;

import com.ita.if103java.ims.dao.AssociateDao;
import com.ita.if103java.ims.dao.ItemDao;
import com.ita.if103java.ims.dao.SavedItemDao;
import com.ita.if103java.ims.dao.WarehouseDao;
import com.ita.if103java.ims.dto.ItemTransactionRequestDto;
import com.ita.if103java.ims.entity.Item;
import com.ita.if103java.ims.entity.SavedItem;
import com.ita.if103java.ims.exception.dao.SavedItemNotFoundException;
import com.ita.if103java.ims.security.UserDetailsImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.List;
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
        if (!(existInAccount(itemTransaction, accountId) &&
            associateDao.findById(accountId, itemTransaction.getAssociateId()).getAccountId().equals(accountId))) {
            throw new SavedItemNotFoundException("Failed to get savedItem during `create` {account_id = " + itemTransaction.getItemDto().getAccountId() + "}");
        }
    }
    @Override
    public void validateInputsMove(ItemTransactionRequestDto itemTransaction, Long accountId) {
        if (!(existInAccount(itemTransaction, accountId))) {
            throw new SavedItemNotFoundException("Failed to get savedItem during `move` {account_id = " + itemTransaction.getItemDto().getAccountId() + "}");
        }
    }
    @Override
    public void validateInputsOut(ItemTransactionRequestDto itemTransaction, UserDetailsImpl user) {
        if (!(itemDao.isExistItemById(itemTransaction.getItemDto().getId(), user.getUser().getAccountId())
            && associateDao.findById(user.getUser().getAccountId(), itemTransaction.getAssociateId()).getAccountId().equals(user.getUser().getAccountId()))) {
            throw new SavedItemNotFoundException("Failed to get savedItem during `outcomeItem` {account_id = " + user.getUser().getAccountId() +
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
            StringBuilder itemsId = new StringBuilder();
            List<SavedItem> savedItem =  savedItemDao.findSavedItemByWarehouseId(warehouseId);
            savedItem.forEach(x->{
                itemsId.append(" id = ");
                itemsId.append(x.getItemId());
                itemsId.append(" or");
            });
            itemsId.delete(itemsId.length()-2, itemsId.length());
            List<Item> item = itemDao.findItemsById(itemsId, accountId);
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