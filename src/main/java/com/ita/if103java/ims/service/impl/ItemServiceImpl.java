package com.ita.if103java.ims.service.impl;

import com.ita.if103java.ims.dao.ItemDao;
import com.ita.if103java.ims.dao.SavedItemDao;
import com.ita.if103java.ims.dao.WarehouseDao;
import com.ita.if103java.ims.dto.ItemDto;
import com.ita.if103java.ims.dto.SavedItemDto;
import com.ita.if103java.ims.entity.Item;
import com.ita.if103java.ims.entity.SavedItem;
import com.ita.if103java.ims.entity.Warehouse;
import com.ita.if103java.ims.mapper.ItemDtoMapper;
import com.ita.if103java.ims.mapper.SavedItemDtoMapper;
import com.ita.if103java.ims.service.ItemService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class ItemServiceImpl implements ItemService {
    private ItemDtoMapper itemDtoMapper;
    private SavedItemDtoMapper savedItemDtoMapper;
    private ItemDao itemDao;
    private SavedItemDao savedItemDao;
    private WarehouseDao warehouseDao;

    @Autowired
    public ItemServiceImpl(ItemDtoMapper itemDtoMapper, SavedItemDtoMapper savedItemDtoMapper, ItemDao itemDao, SavedItemDao savedItemDao, WarehouseDao warehouseDao) {
        this.itemDtoMapper = itemDtoMapper;
        this.savedItemDtoMapper = savedItemDtoMapper;
        this.itemDao = itemDao;
        this.savedItemDao = savedItemDao;
        this.warehouseDao = warehouseDao;
    }


    @Override
    public List<ItemDto> findItems() {
        return itemDtoMapper.convertToItemDtos(itemDao.getItems());

    }


    @Override
    public List<Warehouse> findUseFullWarehouses(SavedItemDto savedItemDto) {
        int capacity = savedItemDto.getItemDto().getVolume() * savedItemDto.getQuantity();
        return warehouseDao.findAll().stream().filter(x -> x.getCapacity() >= capacity).collect(Collectors.toList());
    }

    @Override
    public SavedItemDto addItem(SavedItemDto savedItemDto) {
        Item item = itemDtoMapper.convertItemDtoToItem(savedItemDto.getItemDto());
        itemDao.addItem(item);
        SavedItem savedItem = savedItemDtoMapper.convertSavedItemDtoToSavedItem(savedItemDto);
        savedItem.setItemId(itemDao.findItemByName(item.getName()).getId());
        if (warehouseDao.findById(savedItemDto.getWarehouseId()).getCapacity() >= savedItemDto.getItemDto().getVolume() * savedItemDto.getQuantity()) {
            savedItemDao.addSavedItem(savedItem);
            return savedItemDto;
        }
        return null;
    }


    @Override
    public boolean softDelete(ItemDto itemDto) {
        return itemDao.softDeleteItem(itemDto.getName());
    }

    @Override
    public SavedItemDto findByItemDto(ItemDto itemDto) {
        return savedItemDtoMapper.convertSavedItemToSavedItemDto(savedItemDao.findSavedItemById(itemDto.getId()));
    }


    @Override
    public boolean moveItem(Long warehouseId, SavedItemDto savedItemDto) {
        Item item = itemDao.findItemById(savedItemDto.getItemId());

        if (warehouseDao.findById(savedItemDto.getWarehouseId()).getCapacity() >= item.getVolume() * savedItemDto.getQuantity()) {
            return savedItemDao.updateSavedItem(warehouseId, savedItemDto.getId());
        }
        return false;
    }

    public String outComeItem(SavedItemDto savedItemDto, int quantity) {
        if (savedItemDao.findSavedItemByItemId(savedItemDto.getItemDto().getId()).getQuantity() >= quantity && warehouseDao.findById(savedItemDto.getWarehouseId()).getCapacity() >= quantity) {
            savedItemDao.outComeSavedItem(savedItemDtoMapper.convertSavedItemDtoToSavedItem(savedItemDto), savedItemDto.getQuantity() - quantity);
            return "outcome success";
        }
        return "outcome failed. Can't find needed quantity item in warehouse";
    }
}
