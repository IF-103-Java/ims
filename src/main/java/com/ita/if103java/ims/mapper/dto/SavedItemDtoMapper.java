package com.ita.if103java.ims.mapper.dto;

import com.ita.if103java.ims.dto.SavedItemDto;
import com.ita.if103java.ims.entity.SavedItem;
import org.springframework.stereotype.Component;

@Component
public class SavedItemDtoMapper extends AbstractEntityDtoMapper<SavedItem, SavedItemDto> {

    @Override
    public SavedItem toEntity(SavedItemDto dto) {
        if (dto == null){
            return null;
        }
        SavedItem savedItem = new SavedItem();
        savedItem.setId(dto.getId());
        savedItem.setWarehouseId(dto.getWarehouseId());
        savedItem.setQuantity(dto.getQuantity());
        savedItem.setItemId(dto.getItemId());
        return savedItem;
    }

    @Override
    public SavedItemDto toDto(SavedItem entity) {
        if (entity == null){
            return null;
        }
        SavedItemDto savedItemDto = new SavedItemDto();
        savedItemDto.setId(entity.getId());
        savedItemDto.setWarehouseId(entity.getWarehouseId());
        savedItemDto.setItemId(entity.getItemId());
        savedItemDto.setQuantity(entity.getQuantity());
        return savedItemDto;
    }
}
