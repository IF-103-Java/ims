package com.ita.if103java.ims.dao;

import com.ita.if103java.ims.entity.Account;
import com.ita.if103java.ims.entity.Item;
import com.ita.if103java.ims.entity.SavedItem;
import com.ita.if103java.ims.entity.Warehouse;

import java.util.List;

public interface ItemDao {
    List<Item> getItems();
    List<SavedItem> getSavedItems();
    Item findItemById(Long id);
    SavedItem findSavedItemById(Long id);
    Item findItemByAccountId(Long id);
    SavedItem findSavedItemByItemId(Long id);
    SavedItem findSavedItemByWarehouseId(Long id);
    void addItem(String name, String unit, String description, int volume, Long accountId, boolean active);
    void addSavedItem(Long itemId, int quantity, Long warehouseId);
    boolean updateSavedItem(Long warehouseId, Long savedItemId);
    boolean deleteItem(Long itemId);
    boolean deleteSavedItem(Long savedItemId);
}
