package com.ita.if103java.ims.mapper;

import com.ita.if103java.ims.dto.SavedItemDto;
import com.ita.if103java.ims.entity.SavedItem;

import java.util.List;
import java.util.stream.Collectors;

public class SavedItemDtoMapper {
    public SavedItem convertSavedItemDtoToSavedItem(SavedItemDto savedItemDto) {
        SavedItem savedItem = new SavedItem();
        savedItem.setWarehouseId(savedItemDto.getIdWarehouse());
        savedItem.setQuantity(savedItemDto.getQuantity());
        savedItem.setItemId(savedItemDto.getItemId());
        return savedItem;
    }

    public SavedItemDto convertSavedItemToSavedItemDto(SavedItem savedItem) {
        SavedItemDto savedItemDto = new SavedItemDto();
        savedItemDto.setIdWarehouse(savedItem.getWarehouseId());
        savedItemDto.setItemId(savedItem.getItemId());
        savedItemDto.setQuantity(savedItem.getQuantity());
        return savedItemDto;
    }

    public List<SavedItem> convertToSavedItems(List<SavedItemDto> savedItemDtos) {
        return savedItemDtos.stream().map(this::convertSavedItemDtoToSavedItem).collect(Collectors.toList());
    }

    public List<SavedItemDto> convertToSavedItemDtos(List<SavedItem> savedItems) {
        return savedItems.stream().map(this::convertSavedItemToSavedItemDto).collect(Collectors.toList());
    }

}
