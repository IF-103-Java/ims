package com.ita.if103java.ims.service.impl;

import com.ita.if103java.ims.dao.ItemDao;
import com.ita.if103java.ims.dao.SavedItemDao;
import com.ita.if103java.ims.dao.WarehouseDao;
import com.ita.if103java.ims.dto.ItemDto;
import com.ita.if103java.ims.dto.SavedItemDto;
import com.ita.if103java.ims.dto.WarehouseDto;
import com.ita.if103java.ims.dto.WarehouseLoadDto;
import com.ita.if103java.ims.entity.Item;
import com.ita.if103java.ims.entity.SavedItem;
import com.ita.if103java.ims.entity.Warehouse;
import com.ita.if103java.ims.exception.ItemNotEnoughCapacityInWarehouseException;
import com.ita.if103java.ims.exception.ItemNotEnoughQuantityException;
import com.ita.if103java.ims.mapper.ItemDtoMapper;
import com.ita.if103java.ims.mapper.SavedItemDtoMapper;
import com.ita.if103java.ims.mapper.WarehouseDtoMapper;
import com.ita.if103java.ims.service.ItemService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class ItemServiceImpl implements ItemService {
    private ItemDtoMapper itemDtoMapper;
    private SavedItemDtoMapper savedItemDtoMapper;
    private ItemDao itemDao;
    private SavedItemDao savedItemDao;
    private WarehouseDao warehouseDao;
    private WarehouseDtoMapper warehouseDtoMapper;

    @Autowired
    public ItemServiceImpl(ItemDtoMapper itemDtoMapper, SavedItemDtoMapper savedItemDtoMapper, ItemDao itemDao,
                           SavedItemDao savedItemDao, WarehouseDao warehouseDao,
                           WarehouseDtoMapper warehouseDtoMapper) {
        this.itemDtoMapper = itemDtoMapper;
        this.savedItemDtoMapper = savedItemDtoMapper;
        this.itemDao = itemDao;
        this.savedItemDao = savedItemDao;
        this.warehouseDao = warehouseDao;
        this.warehouseDtoMapper = warehouseDtoMapper;
    }


    @Override
    public List<ItemDto> findItems() {
        List<Item> items = itemDao.getItems();
        items.sort(Comparator.comparing(Item::getName));
        return itemDtoMapper.toDtoList(items);

    }

    @Override
    public ItemDto findById(Long id) {
        return itemDtoMapper.toDto(itemDao.findItemById(id));
    }

    @Override
    public SavedItemDto addSavedItem(SavedItemDto savedItemDto) {
        SavedItem savedItem = savedItemDtoMapper.convertSavedItemDtoToSavedItem(savedItemDto);
        savedItem.setItemId(itemDao.findItemByName(savedItemDto.getItemDto().getName()).getId());
        if (isEnoughCapacityInWarehouse(savedItemDto)) {
            savedItemDao.addSavedItem(savedItem);
            return savedItemDto;
        }
        throw new ItemNotEnoughCapacityInWarehouseException("Can't add savedItemDto in warehouse because it doesn't  " +
            "have enough capacity {warehouse_id = " + savedItemDto.getWarehouseId() + "}");

    }

    private boolean isEnoughCapacityInWarehouse(SavedItemDto savedItemDto) {
        return warehouseDao.findById(savedItemDto.getWarehouseId()).getCapacity() >= savedItemDto.getItemDto().getVolume() * savedItemDto.getQuantity();
    }

    @Override
    public List<WarehouseDto> findUsefullWarehouses(SavedItemDto savedItemDto) {
        int capacity = savedItemDto.getItemDto().getVolume() * savedItemDto.getQuantity();
        List<Warehouse> childWarehouses = new ArrayList<>();
        for (Warehouse warehouse : warehouseDao.findAll()) {
            childWarehouses.addAll(warehouseDao.findChildrenByTopWarehouseID(warehouse.getId()).stream().filter(x -> x.getCapacity() >= capacity).collect(Collectors.toList()));
        }
        return warehouseDtoMapper.toDtoList(childWarehouses);

    }

    @Override
    public ItemDto addItem(ItemDto itemDto) {
        itemDao.addItem(itemDtoMapper.convertItemDtoToItem(itemDto));
        return itemDto;
    }

    @Override
    public SavedItemDto findSavedItemById(SavedItemDto savedItemDto) {
        return savedItemDtoMapper.convertSavedItemToSavedItemDto(savedItemDao.findSavedItemById(savedItemDto.getId()));
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
        throw new ItemNotEnoughCapacityInWarehouseException("Can't move savedItemDto in warehouse because it doesn't " +
            " have enough capacity {warehouse_id = " + warehouseLoadDto.getId() + "}");
    }

    @Override
    public SavedItemDto outcomeItem(SavedItemDto savedItemDto, int quantity) {
        int difference = savedItemDto.getQuantity() - quantity;
        if (savedItemDao.findSavedItemByItemId(savedItemDto.getItemId()).getQuantity() >= quantity) {
            savedItemDao.outComeSavedItem(savedItemDtoMapper.convertSavedItemDtoToSavedItem(savedItemDto), difference);
            savedItemDto.setQuantity(difference);
            return savedItemDto;
        }
        throw new ItemNotEnoughQuantityException("Outcome failed. Can't find needed quantity item in warehouse needed" +
            " quantity of items {warehouse_id = " + savedItemDto.getId() + ", quantity = " + quantity + "}");
    }

}
