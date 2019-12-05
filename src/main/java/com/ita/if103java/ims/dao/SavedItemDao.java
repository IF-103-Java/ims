package com.ita.if103java.ims.dao;

import com.ita.if103java.ims.entity.SavedItem;

import java.util.List;

public interface SavedItemDao {
    List<SavedItem> getSavedItems();

    SavedItem findSavedItemById(Long id);

    SavedItem findSavedItemByItemId(Long id);

    SavedItem findSavedItemByWarehouseId(Long id);

    SavedItem addSavedItem(SavedItem savedItem);

    boolean updateSavedItem(Long warehouseId, Long savedItemId);

    boolean deleteSavedItem(Long savedItemId);
}
