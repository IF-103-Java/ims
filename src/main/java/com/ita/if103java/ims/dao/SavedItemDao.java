package com.ita.if103java.ims.dao;

import com.ita.if103java.ims.entity.SavedItem;

import java.util.List;
import java.util.Optional;

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

    Optional<SavedItem> findSavedItemByItemIdAndWarehouseId(Long itemId, Long warehouseId);

    boolean deleteSavedItemById(Long savedItem);
}
