package com.ita.if103java.ims.service;

import com.ita.if103java.ims.dto.ItemDto;
import com.ita.if103java.ims.dto.SavedItemDto;
import com.ita.if103java.ims.entity.Warehouse;

import java.util.List;


public interface ItemService {
    List<ItemDto> findItems();

    SavedItemDto addItem(SavedItemDto savedItemDto);

    boolean softDelete(ItemDto itemDto);

    SavedItemDto findByItemDto(ItemDto itemDto);

    List<Warehouse> findUseFullWarehouses(SavedItemDto savedItemDto);

    boolean moveItem(Long warehouseId, SavedItemDto savedItemDto);
}
