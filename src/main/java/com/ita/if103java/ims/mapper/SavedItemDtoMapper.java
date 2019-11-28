package com.ita.if103java.ims.mapper;

import com.ita.if103java.ims.dto.ItemDto;
import com.ita.if103java.ims.dto.SavedItemDto;
import com.ita.if103java.ims.entity.Item;
import com.ita.if103java.ims.entity.SavedItem;
import com.ita.if103java.ims.entity.Warehouse;

import java.util.List;
import java.util.stream.Collectors;

public class SavedItemDtoMapper {
    public SavedItem convertSavedItemDtoToSavedItem(SavedItemDto savedItemDto){
        SavedItem savedItem = new SavedItem();
        Warehouse warehouse = new Warehouse();
        warehouse.setId(savedItemDto.getIdWarehouse());
       savedItem.setWarehouse(warehouse);
       savedItem.setQuantity(savedItemDto.getQuantity());
       Item item = new Item();
       item.setId(savedItemDto.getItemId());
       savedItem.setItem(item);
        return savedItem;
    }
    public SavedItemDto convertSavedItemToSavedItemDto(SavedItem savedItem){
      SavedItemDto savedItemDto = new SavedItemDto();
      savedItemDto.setIdWarehouse(savedItem.getItem().getId());
      savedItemDto.setItemId(savedItem.getItem().getId());
      savedItemDto.setQuantity(savedItem.getQuantity());
      return savedItemDto;
    }
    public List<SavedItem> convertToSavedItems(List<SavedItemDto> savedItemDtos){
       return savedItemDtos.stream().map(this::convertSavedItemDtoToSavedItem).collect(Collectors.toList());
    }
    public List<SavedItemDto> convertToSavedItemDtos(List<SavedItem> savedItems){
        return savedItems.stream().map(this::convertSavedItemToSavedItemDto).collect(Collectors.toList());
    }

}
