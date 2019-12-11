package com.ita.if103java.ims.mapper;

import com.ita.if103java.ims.dto.ItemDto;
import com.ita.if103java.ims.entity.Item;
import org.springframework.stereotype.Component;

@Component
public class ItemDtoMapper extends AbstractEntityDtoMapper<Item, ItemDto> {
    public Item convertItemDtoToItem(ItemDto itemDto) {
        Item item = new Item();
        item.setName(itemDto.getName());
        item.setActive(itemDto.isActive());
        item.setDescription(itemDto.getDescription());
        item.setVolume(itemDto.getVolume());
        item.setUnit(itemDto.getUnit());
        item.setId(itemDto.getId());
        item.setAccountId(itemDto.getAccountId());
        return item;
    }

    public ItemDto convertItemToItemDto(Item item) {
        ItemDto itemDto = new ItemDto();
        itemDto.setName(item.getName());
        itemDto.setActive(item.isActive());
        itemDto.setDescription(item.getDescription());
        itemDto.setVolume(item.getVolume());
        itemDto.setUnit(item.getUnit());
        itemDto.setId(item.getId());
        itemDto.setAccountId(item.getAccountId());
        return itemDto;
    }

    @Override
    public Item toEntity(ItemDto dto) {
        return convertItemDtoToItem(dto);
    }

    @Override
    public ItemDto toDto(Item entity) {
        return convertItemToItemDto(entity);
    }
}
