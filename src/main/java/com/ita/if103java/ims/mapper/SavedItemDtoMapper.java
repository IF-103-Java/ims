package com.ita.if103java.ims.mapper;

import com.ita.if103java.ims.dto.SavedItemDto;
import com.ita.if103java.ims.entity.SavedItem;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class SavedItemDtoMapper {
    public SavedItem convertSavedItemDtoToSavedItem(SavedItemDto savedItemDto) {
        SavedItem savedItem = new SavedItem();
        savedItem.setId(savedItemDto.getId());
        savedItem.setWarehouseId(savedItemDto.getWarehouseId());
        savedItem.setQuantity(savedItemDto.getQuantity());
        savedItem.setItemId(savedItemDto.getItemId());
        savedItem.setWarehouse(savedItemDto.getWarehouse());
        return savedItem;
    }

    public SavedItemDto convertSavedItemToSavedItemDto(SavedItem savedItem) {
        SavedItemDto savedItemDto = new SavedItemDto();
        savedItemDto.setId(savedItem.getId());
        savedItemDto.setWarehouseId(savedItem.getWarehouseId());
        savedItemDto.setItemId(savedItem.getItemId());
        savedItemDto.setQuantity(savedItem.getQuantity());
        savedItemDto.setWarehouse(savedItem.getWarehouse());
        return savedItemDto;
    }

    public List<SavedItem> convertToSavedItems(List<SavedItemDto> savedItemDtos) {
        return savedItemDtos.stream().map(this::convertSavedItemDtoToSavedItem).collect(Collectors.toList());
    }

    public List<SavedItemDto> convertToSavedItemDtos(List<SavedItem> savedItems) {
        return savedItems.stream().map(this::convertSavedItemToSavedItemDto).collect(Collectors.toList());
    }

}
