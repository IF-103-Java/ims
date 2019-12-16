package com.ita.if103java.ims.service;

import com.ita.if103java.ims.dto.ItemDto;
import com.ita.if103java.ims.dto.SavedItemDto;
import com.ita.if103java.ims.dto.WarehouseDto;

import java.util.List;


public interface ItemService {
    List<ItemDto> findItems();

    List<ItemDto> findItemsByParam(String param);

    ItemDto findById(Long id);

    SavedItemDto addSavedItem(SavedItemDto savedItemDto);

    ItemDto addItem(ItemDto itemDto);

    SavedItemDto findSavedItemById(SavedItemDto savedItemDto);

    boolean softDelete(ItemDto itemDto);

    SavedItemDto findByItemDto(ItemDto itemDto);

    List<WarehouseDto> findUsefullWarehouses(SavedItemDto savedItemDto);

    boolean moveItem(WarehouseDto warehouseDto, SavedItemDto savedItemDto);

    SavedItemDto outcomeItem(SavedItemDto savedItemDto, int quantity);

}
