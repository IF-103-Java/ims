package com.ita.if103java.ims.mapper;

import com.ita.if103java.ims.dto.SavedItemDto;
import com.ita.if103java.ims.entity.SavedItem;
import org.springframework.stereotype.Component;

@Component
public class SavedItemDtoMapper extends AbstractEntityDtoMapper<SavedItem, SavedItemDto> {
    public SavedItem convertSavedItemDtoToSavedItem(SavedItemDto savedItemDto) {
        SavedItem savedItem = new SavedItem();
        savedItem.setId(savedItemDto.getId());
        savedItem.setWarehouseId(savedItemDto.getWarehouseId());
        savedItem.setQuantity(savedItemDto.getQuantity());
        savedItem.setItemId(savedItemDto.getItemId());
        return savedItem;
    }

    public SavedItemDto convertSavedItemToSavedItemDto(SavedItem savedItem) {
        SavedItemDto savedItemDto = new SavedItemDto();
        savedItemDto.setId(savedItem.getId());
        savedItemDto.setWarehouseId(savedItem.getWarehouseId());
        savedItemDto.setItemId(savedItem.getItemId());
        savedItemDto.setQuantity(savedItem.getQuantity());
        return savedItemDto;
    }


    @Override
    public SavedItem toEntity(SavedItemDto dto) {
        return convertSavedItemDtoToSavedItem(dto);
    }

    @Override
    public SavedItemDto toDto(SavedItem entity) {
        return convertSavedItemToSavedItemDto(entity);
    }
}
