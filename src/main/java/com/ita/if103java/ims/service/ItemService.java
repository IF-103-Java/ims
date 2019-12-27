package com.ita.if103java.ims.service;

import com.ita.if103java.ims.dto.ItemDto;
import com.ita.if103java.ims.dto.ItemTransactionRequestDto;
import com.ita.if103java.ims.dto.SavedItemDto;
import com.ita.if103java.ims.dto.WarehouseDto;
import com.ita.if103java.ims.entity.User;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface ItemService {

    List<ItemDto> findSortedItem(Pageable pageable);

    ItemDto findById(Long id);

    SavedItemDto addSavedItem(ItemTransactionRequestDto itemTransaction, User user);

    ItemDto addItem(ItemDto itemDto, User user);

    SavedItemDto findSavedItemById(Long id);

    boolean softDelete(Long id);

    List<SavedItemDto> findByItemId(Long id);

    List<WarehouseDto> findUsefullWarehouses(int volume, int quantity);

    boolean moveItem(ItemTransactionRequestDto itemTransaction, User user);

    SavedItemDto outcomeItem(ItemTransactionRequestDto itemTransaction, long quantity, User user);

}
