package com.ita.if103java.ims.mapper;

import com.ita.if103java.ims.dao.ItemDao;
import com.ita.if103java.ims.dto.ItemDto;
import com.ita.if103java.ims.entity.Item;

public class ItemDtoMapper {
    public Item convertItemDtoToItem(ItemDto itemDto){
      Item item = new Item();
      item.setName(itemDto.getName());
      item.setActive(itemDto.isActive());
      item.setDescription(itemDto.getDescription());
      item.setVolume(itemDto.getVolume());
      item.setUnit(itemDto.getUnit());
      return item;
    }
    public ItemDto convertItemDtoToItemDto(Item item){
        ItemDto itemDto = new ItemDto();
        itemDto.setName(item.getName());
        itemDto.setActive(item.isActive());
        itemDto.setDescription(item.getDescription());
        itemDto.setVolume(item.getVolume());
        itemDto.setUnit(item.getUnit());
        return itemDto;
    }
}
