package com.ita.if103java.ims.service.impl;

import com.ita.if103java.ims.dao.ItemDao;
import com.ita.if103java.ims.dao.SavedItemDao;
import com.ita.if103java.ims.dao.WarehouseDao;
import com.ita.if103java.ims.dto.ItemDto;
import com.ita.if103java.ims.dto.SavedItemDto;
import com.ita.if103java.ims.dto.WarehouseLoadDto;
import com.ita.if103java.ims.entity.SavedItem;
import com.ita.if103java.ims.entity.Warehouse;
import com.ita.if103java.ims.exception.ItemNotEnoughCapacityInWarehouseException;
import com.ita.if103java.ims.exception.ItemNotEnoughQuantityException;
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
    public SavedItemDto addSavedItem(SavedItemDto savedItemDto) {
        SavedItem savedItem = savedItemDtoMapper.convertSavedItemDtoToSavedItem(savedItemDto);
        savedItem.setItemId(itemDao.findItemByName(savedItemDto.getItemDto().getName()).getId());
        if (isEnoughCapacityInWarehouse(savedItemDto)) {
            savedItemDao.addSavedItem(savedItem);
            return savedItemDto;
        }
        throw new ItemNotEnoughCapacityInWarehouseException("Can't add savedItemDto in warehouse because it doesn't  have enough capacity {warehouse_id = " + savedItemDto.getWarehouseId() + "}");

    }
    private boolean isEnoughCapacityInWarehouse(SavedItemDto savedItemDto){
        return warehouseDao.findById(savedItemDto.getWarehouseId()).getCapacity() >= savedItemDto.getItemDto().getVolume() * savedItemDto.getQuantity();
    }

    @Override
    public List<WarehouseLoadDto> findUseFullWarehouses(SavedItemDto savedItemDto) {
        int capacity = savedItemDto.getItemDto().getVolume() * savedItemDto.getQuantity();
//        return warehouseDao.findAll().stream().filter(x -> x.getCapacity() >= capacity).collect(Collectors.toList());
        return null;
    }

    @Override
    public ItemDto addItem(ItemDto itemDto) {
      itemDao.addItem(itemDtoMapper.convertItemDtoToItem(itemDto));
     return itemDto;
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
    public boolean moveItem(WarehouseLoadDto warehouseLoadDto, SavedItemDto savedItemDto) {
        savedItemDto.setItemDto(itemDtoMapper.convertItemToItemDto(itemDao.findItemById(savedItemDto.getId())));
        if (isEnoughCapacityInWarehouse(savedItemDto)) {
            return savedItemDao.updateSavedItem(warehouseLoadDto.getId(), savedItemDto.getId());
        }
       throw new ItemNotEnoughCapacityInWarehouseException("Can't move savedItemDto in warehouse because it doesn't  have enough capacity {warehouse_id = " + warehouseLoadDto.getId() + "}");
    }
    @Override
    public SavedItemDto outcomeItem(SavedItemDto savedItemDto, int quantity) {
        int difference = savedItemDto.getQuantity()-quantity;
        if (savedItemDao.findSavedItemByItemId(savedItemDto.getItemId()).getQuantity() >= quantity) {
            savedItemDao.outComeSavedItem(savedItemDtoMapper.convertSavedItemDtoToSavedItem(savedItemDto), difference);
            savedItemDto.setQuantity(difference);
            return savedItemDto;
        }
        throw new ItemNotEnoughQuantityException("Outcome failed. Can't find needed quantity item in warehouse needed quantity of items {warehouse_id = " + savedItemDto.getId() + "quantity"+ quantity+"}");
    }
}
