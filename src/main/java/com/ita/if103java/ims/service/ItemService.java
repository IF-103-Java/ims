package com.ita.if103java.ims.service;

import com.ita.if103java.ims.dto.*;
import com.ita.if103java.ims.entity.Associate;
import com.ita.if103java.ims.entity.User;
import org.springframework.data.domain.Pageable;
import java.util.List;

public interface ItemService {

    List<ItemDto> findSortedItem(Pageable pageable);

    ItemDto findById(Long id);

    SavedItemDto addSavedItem(SavedItemDto savedItemDto, User user, Associate associate);

    ItemDto addItem(ItemDto itemDto, User user);

    SavedItemDto findSavedItemById(Long id);

    boolean softDelete(Long id);

    List<SavedItemDto> findByItemId(Long id);

    List<WarehouseDto> findUsefullWarehouses(int volume, int quantity);

    boolean moveItem(SavedItemDto savedItemDto, Long id, User user, Associate associate);

    SavedItemDto outcomeItem(SavedItemDto savedItemDto, int quantity, User user, Associate associate);

}
