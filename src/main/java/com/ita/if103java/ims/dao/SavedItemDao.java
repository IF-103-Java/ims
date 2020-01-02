package com.ita.if103java.ims.dao;

import com.ita.if103java.ims.entity.SavedItem;

import java.util.List;

public interface SavedItemDao {
    List<SavedItem> getSavedItems();

    SavedItem findSavedItemById(Long id);

    List<SavedItem> findSavedItemByItemId(Long id);

    List<SavedItem> findSavedItemByWarehouseId(Long id);
    boolean existSavedItemByWarehouseId(Long id);

    SavedItem addSavedItem(SavedItem savedItem);

    boolean outComeSavedItem(SavedItem savedItem, int quantity);

    boolean updateSavedItem(Long warehouseId, Long savedItemId);

    boolean deleteSavedItem(Long savedItemId);
}
