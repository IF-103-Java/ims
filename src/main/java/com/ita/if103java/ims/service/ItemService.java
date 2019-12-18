package com.ita.if103java.ims.service;

import com.ita.if103java.ims.dto.ItemDto;
import com.ita.if103java.ims.dto.SavedItemDto;
import com.ita.if103java.ims.dto.WarehouseDto;
import org.springframework.data.domain.Pageable;

import java.util.List;


public interface ItemService {

    List<ItemDto> findSortedItem(Pageable pageable);

    ItemDto findById(Long id);

    SavedItemDto addSavedItem(SavedItemDto savedItemDto);

    ItemDto addItem(ItemDto itemDto);

    SavedItemDto findSavedItemById(Long id);

    boolean softDelete(Long id);

    SavedItemDto findByItemId(Long id);

    List<WarehouseDto> findUsefullWarehouses(int volume, int quantity);

    boolean moveItem(SavedItemDto savedItemDto, Long id);

    SavedItemDto outcomeItem(SavedItemDto savedItemDto, int quantity);

}
