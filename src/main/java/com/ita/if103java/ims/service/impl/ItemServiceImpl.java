package com.ita.if103java.ims.service.impl;

import com.ita.if103java.ims.dao.ItemDao;
import com.ita.if103java.ims.dao.SavedItemDao;
import com.ita.if103java.ims.dao.WarehouseDao;
import com.ita.if103java.ims.dto.ItemDto;
import com.ita.if103java.ims.dto.SavedItemDto;
import com.ita.if103java.ims.dto.WarehouseDto;
import com.ita.if103java.ims.entity.SavedItem;
import com.ita.if103java.ims.entity.Warehouse;
import com.ita.if103java.ims.exception.ItemNotEnoughCapacityInWarehouseException;
import com.ita.if103java.ims.exception.ItemNotEnoughQuantityException;
import com.ita.if103java.ims.mapper.ItemDtoMapper;
import com.ita.if103java.ims.mapper.SavedItemDtoMapper;
import com.ita.if103java.ims.mapper.WarehouseDtoMapper;
import com.ita.if103java.ims.service.ItemService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
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
    public List<ItemDto> findSortedItem(Pageable pageable) {
        return itemDtoMapper.toDtoList(itemDao.getItems(pageable));
    }

    @Override
    public ItemDto findById(Long id) {
        return itemDtoMapper.toDto(itemDao.findItemById(id));

    }

    @Override
    public SavedItemDto addSavedItem(SavedItemDto savedItemDto) {
        SavedItem savedItem = savedItemDtoMapper.toEntity(savedItemDto);
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
    public List<WarehouseDto> findUsefullWarehouses(int volume, int quantity) {
        int capacity = volume * quantity;
        List<Warehouse> childWarehouses = new ArrayList<>();
        for (Warehouse warehouse : warehouseDao.findAll()) {
            childWarehouses.addAll(warehouseDao.findChildrenByTopWarehouseID(warehouse.getId()).stream().filter(x -> x.getCapacity() >= capacity).collect(Collectors.toList()));
        }
        return warehouseDtoMapper.toDtoList(childWarehouses);

    }

    @Override
    public ItemDto addItem(ItemDto itemDto) {

        itemDao.addItem(itemDtoMapper.toEntity(itemDto));
        return itemDto;
    }

    @Override
    public SavedItemDto findSavedItemById(Long id) {
        return savedItemDtoMapper.toDto(savedItemDao.findSavedItemById(id));
    }


    @Override
    public boolean softDelete(Long id) {
        return itemDao.softDeleteItem(id);
    }

    @Override
    public SavedItemDto findByItemId(Long id) {
        return savedItemDtoMapper.toDto(savedItemDao.findSavedItemById(id));
    }


    @Override
    public boolean moveItem(SavedItemDto savedItemDto, Long id) {
        savedItemDto.setItemDto(itemDtoMapper.toDto(itemDao.findItemById(savedItemDto.getId())));
        if (isEnoughCapacityInWarehouse(savedItemDto)) {
            return savedItemDao.updateSavedItem(id, savedItemDto.getId());
        }
        throw new ItemNotEnoughCapacityInWarehouseException("Can't move savedItemDto in warehouse because it doesn't " +
            " have enough capacity {warehouse_id = " + id + "}");
    }

    @Override
    public SavedItemDto outcomeItem(SavedItemDto savedItemDto, int quantity) {
        int difference = savedItemDto.getQuantity() - quantity;
        if (savedItemDao.findSavedItemByItemId(savedItemDto.getItemId()).getQuantity() >= quantity) {
            savedItemDao.outComeSavedItem(savedItemDtoMapper.toEntity(savedItemDto), difference);
            savedItemDto.setQuantity(difference);
            return savedItemDto;
        }
        throw new ItemNotEnoughQuantityException("Outcome failed. Can't find needed quantity item in warehouse needed" +
            " quantity of items {warehouse_id = " + savedItemDto.getId() + ", quantity = " + quantity + "}");
    }

}
