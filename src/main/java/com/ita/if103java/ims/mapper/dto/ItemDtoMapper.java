package com.ita.if103java.ims.mapper.dto;

import com.ita.if103java.ims.dto.ItemDto;
import com.ita.if103java.ims.entity.Item;
import org.springframework.stereotype.Component;

@Component
public class ItemDtoMapper extends AbstractEntityDtoMapper<Item, ItemDto> {

    @Override
    public Item toEntity(ItemDto dto) {
        if (dto == null) {
            return null;
        }
        Item item = new Item();
        item.setName(dto.getName());
        item.setActive(dto.isActive());
        item.setDescription(dto.getDescription());
        item.setVolume(dto.getVolume());
        item.setUnit(dto.getUnit());
        item.setId(dto.getId());
        item.setAccountId(dto.getAccountId());
        return item;
    }

    @Override
    public ItemDto toDto(Item entity) {
        if (entity == null) {
            return null;
        }
        ItemDto itemDto = new ItemDto();
        itemDto.setName(entity.getName());
        itemDto.setActive(entity.isActive());
        itemDto.setDescription(entity.getDescription());
        itemDto.setVolume(entity.getVolume());
        itemDto.setUnit(entity.getUnit());
        itemDto.setId(entity.getId());
        itemDto.setAccountId(entity.getAccountId());
        return itemDto;
    }
}
