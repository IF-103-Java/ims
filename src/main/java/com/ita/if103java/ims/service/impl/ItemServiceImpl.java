package com.ita.if103java.ims.service.impl;

import com.ita.if103java.ims.dao.ItemDao;
import com.ita.if103java.ims.dao.SavedItemDao;
import com.ita.if103java.ims.dao.TransactionDao;
import com.ita.if103java.ims.dao.WarehouseDao;
import com.ita.if103java.ims.dto.*;
import com.ita.if103java.ims.entity.*;
import com.ita.if103java.ims.exception.ItemNotEnoughCapacityInWarehouseException;
import com.ita.if103java.ims.exception.ItemNotEnoughQuantityException;
import com.ita.if103java.ims.mapper.ItemDtoMapper;
import com.ita.if103java.ims.mapper.SavedItemDtoMapper;
import com.ita.if103java.ims.mapper.WarehouseDtoMapper;
import com.ita.if103java.ims.service.EventService;
import com.ita.if103java.ims.service.ItemService;
import com.ita.if103java.ims.util.ItemEventUtil;
import com.ita.if103java.ims.util.ItemTransactionUtil;
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
    private TransactionDao transactionDao;
    private EventService eventService;

    @Autowired
    public ItemServiceImpl(ItemDtoMapper itemDtoMapper, SavedItemDtoMapper savedItemDtoMapper, ItemDao itemDao, SavedItemDao savedItemDao, WarehouseDao warehouseDao, WarehouseDtoMapper warehouseDtoMapper, TransactionDao transactionDao, EventService eventService) {
        this.itemDtoMapper = itemDtoMapper;
        this.savedItemDtoMapper = savedItemDtoMapper;
        this.itemDao = itemDao;
        this.savedItemDao = savedItemDao;
        this.warehouseDao = warehouseDao;
        this.warehouseDtoMapper = warehouseDtoMapper;
        this.transactionDao = transactionDao;
        this.eventService = eventService;
    }

    @Override
    public List<ItemDto> findSortedItem(Pageable pageable) {
        String sort = pageable.getSort().toString().replaceAll(": ", " ");
        return itemDtoMapper.toDtoList(itemDao.getItems(sort, pageable.getPageSize(), pageable.getOffset()));
    }

    @Override
    public ItemDto findById(Long id) {
        return itemDtoMapper.toDto(itemDao.findItemById(id));

    }

    @Override
    public SavedItemDto addSavedItem(SavedItemDto savedItemDto, User user, Associate associate) {
        SavedItem savedItem = savedItemDtoMapper.toEntity(savedItemDto);
        savedItem.setItemId(itemDao.findItemByName(savedItemDto.getItemDto().getName()).getId());
        if (isEnoughCapacityInWarehouse(savedItemDto)) {
            savedItemDao.addSavedItem(savedItem);
            transactionDao.create(ItemTransactionUtil.createTransaction(savedItemDto, user, associate, TransactionType.IN));
            eventService.create(ItemEventUtil.createEvent(savedItemDto, user, EventName.ITEM_CAME));
            return savedItemDto;
        }
        eventService.create(ItemEventUtil.createEvent(savedItemDto, user, EventName.LOW_SPACE_IN_WAREHOUSE));
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
    public ItemDto addItem(ItemDto itemDto, User user) {
        itemDto.setAccountId(user.getAccountId());
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
    public List<SavedItemDto> findByItemId(Long id) {
        return savedItemDtoMapper.toDtoList(savedItemDao.findSavedItemByItemId(id));
    }


    @Override
    public boolean moveItem(SavedItemDto savedItemDto, Long id, User user, Associate associate) {
        savedItemDto.setItemDto(itemDtoMapper.toDto(itemDao.findItemById(savedItemDto.getId())));
        if (isEnoughCapacityInWarehouse(savedItemDto)) {
            eventService.create(ItemEventUtil.createEvent(savedItemDto, user, EventName.ITEM_MOVED));
            transactionDao.create(ItemTransactionUtil.createTransaction(savedItemDto, user, associate, TransactionType.MOVE));
            eventService.create(ItemEventUtil.createEvent(savedItemDto, user, EventName.ITEM_MOVED));
            return savedItemDao.updateSavedItem(id, savedItemDto.getId());
        }
        eventService.create(ItemEventUtil.createEvent(savedItemDto, user, EventName.LOW_SPACE_IN_WAREHOUSE));
        throw new ItemNotEnoughCapacityInWarehouseException("Can't move savedItemDto in warehouse because it doesn't " +
            " have enough capacity {warehouse_id = " + id + "}");
    }

    @Override
    public SavedItemDto outcomeItem(SavedItemDto savedItemDto, int quantity, User user, Associate associate) {
        int difference = savedItemDto.getQuantity() - quantity;
        if (savedItemDao.findSavedItemByWarehouseId(savedItemDto.getWarehouseId()).getQuantity() >= quantity) {
            savedItemDao.outComeSavedItem(savedItemDtoMapper.toEntity(savedItemDto), difference);
            transactionDao.create(ItemTransactionUtil.createTransaction(savedItemDto, user, associate, TransactionType.OUT));
            eventService.create(ItemEventUtil.createEvent(savedItemDto, user, EventName.ITEM_SHIPPED));
            savedItemDto.setQuantity(difference);
            return savedItemDto;
        }
        eventService.create(ItemEventUtil.createEvent(savedItemDto, user, EventName.ITEM_ENDED));
        throw new ItemNotEnoughQuantityException("Outcome failed. Can't find needed quantity item in warehouse needed" +
            " quantity of items {warehouse_id = " + savedItemDto.getId() + ", quantity = " + quantity + "}");
    }

}
