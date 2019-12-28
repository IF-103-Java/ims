package com.ita.if103java.ims.service.impl;


import com.ita.if103java.ims.dao.ItemDao;
import com.ita.if103java.ims.dao.SavedItemDao;
import com.ita.if103java.ims.dao.TransactionDao;
import com.ita.if103java.ims.dao.WarehouseDao;
import com.ita.if103java.ims.dao.AssociateDao;
import com.ita.if103java.ims.dto.ItemDto;
import com.ita.if103java.ims.dto.ItemTransactionRequestDto;
import com.ita.if103java.ims.dto.SavedItemDto;
import com.ita.if103java.ims.dto.WarehouseDto;


import com.ita.if103java.ims.entity.SavedItem;
import com.ita.if103java.ims.entity.Transaction;
import com.ita.if103java.ims.entity.TransactionType;
import com.ita.if103java.ims.entity.User;
import com.ita.if103java.ims.entity.Event;
import com.ita.if103java.ims.entity.EventName;
import com.ita.if103java.ims.entity.Warehouse;
import com.ita.if103java.ims.exception.ItemNotEnoughCapacityInWarehouseException;
import com.ita.if103java.ims.exception.ItemNotEnoughQuantityException;
import com.ita.if103java.ims.mapper.ItemDtoMapper;
import com.ita.if103java.ims.mapper.SavedItemDtoMapper;
import com.ita.if103java.ims.mapper.WarehouseDtoMapper;
import com.ita.if103java.ims.service.EventService;
import com.ita.if103java.ims.service.ItemService;
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
    private AssociateDao associateDao;

    @Autowired
    public ItemServiceImpl(ItemDtoMapper itemDtoMapper, SavedItemDtoMapper savedItemDtoMapper, ItemDao itemDao, SavedItemDao savedItemDao, WarehouseDao warehouseDao, WarehouseDtoMapper warehouseDtoMapper, TransactionDao transactionDao, EventService eventService, AssociateDao associateDao) {
        this.itemDtoMapper = itemDtoMapper;
        this.savedItemDtoMapper = savedItemDtoMapper;
        this.itemDao = itemDao;
        this.savedItemDao = savedItemDao;
        this.warehouseDao = warehouseDao;
        this.warehouseDtoMapper = warehouseDtoMapper;
        this.transactionDao = transactionDao;
        this.eventService = eventService;
        this.associateDao = associateDao;
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
    public SavedItemDto addSavedItem(ItemTransactionRequestDto itemTransaction, User user) {
        SavedItem savedItem = new SavedItem(itemTransaction.getItemDto().getId(), itemTransaction.getQuantity().intValue(), itemTransaction.getSourceWarehouseId());
        if (isEnoughCapacityInWarehouse(itemTransaction)) {
            SavedItemDto savedItemDto = savedItemDtoMapper.toDto(savedItemDao.addSavedItem(savedItem));
            Transaction transaction = transactionDao.create(ItemTransactionUtil.createTransaction(itemTransaction, user, itemTransaction.getAssociateId(), TransactionType.IN));
            eventService.create(new Event("Moved " + itemTransaction.getQuantity() + " " + itemTransaction.getItemDto().getName() +
                " to warehouse " + warehouseDao.findById(itemTransaction.getDestinationWarehouseId()).getName() + " from supplier " + associateDao.findById(itemTransaction.getAssociateId()).getName(), user.getAccountId(),
                itemTransaction.getDestinationWarehouseId(), user.getId(), EventName.ITEM_CAME, transaction.getId().longValue()));
            return savedItemDto;
        }
        eventService.create(new Event("Not enough capacity! Capacity " + warehouseDao.findById(itemTransaction.getDestinationWarehouseId()).getCapacity() +
            " in Warehouse " + warehouseDao.findById(itemTransaction.getDestinationWarehouseId()).getName(), user.getAccountId(),
            itemTransaction.getDestinationWarehouseId(), user.getId(), EventName.LOW_SPACE_IN_WAREHOUSE, null));
        throw new ItemNotEnoughCapacityInWarehouseException("Can't add savedItemDto in warehouse because it doesn't  " +
            "have enough capacity {warehouse_id = " + itemTransaction.getDestinationWarehouseId() + "}");

    }

    private boolean isEnoughCapacityInWarehouse(ItemTransactionRequestDto itemTransaction) {
        return warehouseDao.findById(itemTransaction.getDestinationWarehouseId()).getCapacity() >= itemTransaction.getItemDto().getVolume() * itemTransaction.getQuantity();
    }

    @Override
    public List<WarehouseDto> findUsefullWarehouses(int volume, int quantity) {
        int capacity = volume * quantity;
        List<Warehouse> childWarehouses = new ArrayList<>();
        for (Warehouse warehouse : warehouseDao.findAll(Pageable.unpaged())) {
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
    public boolean moveItem(ItemTransactionRequestDto itemTransaction, User user) {
        if (isEnoughCapacityInWarehouse(itemTransaction)) {
            Transaction transaction = transactionDao.create(ItemTransactionUtil.createTransaction(itemTransaction, user, itemTransaction.getAssociateId(), TransactionType.MOVE));
            eventService.create(new Event("Moved " + itemTransaction.getQuantity() + " " + itemTransaction.getItemDto().getName() +
                " from warehouse " + warehouseDao.findById(itemTransaction.getSourceWarehouseId()).getName() + " to warehouse " + warehouseDao.findById(itemTransaction.getDestinationWarehouseId()).getName(), user.getAccountId(),
                itemTransaction.getSourceWarehouseId(), user.getId(), EventName.ITEM_MOVED, transaction.getId().longValue()));
            return savedItemDao.updateSavedItem(itemTransaction.getDestinationWarehouseId(), itemTransaction.getSourceWarehouseId());
        }
        eventService.create(new Event("Not enough capacity! Capacity " + warehouseDao.findById(itemTransaction.getDestinationWarehouseId()).getCapacity() +
            " in warehouse " + warehouseDao.findById(itemTransaction.getDestinationWarehouseId()).getName(), user.getAccountId(),
            itemTransaction.getDestinationWarehouseId(), user.getId(), EventName.LOW_SPACE_IN_WAREHOUSE, null));
        throw new ItemNotEnoughCapacityInWarehouseException("Can't move savedItemDto in warehouse because it doesn't " +
            " have enough capacity {warehouse_id = " + itemTransaction.getDestinationWarehouseId() + "}");
    }

    @Override
    public SavedItemDto outcomeItem(ItemTransactionRequestDto itemTransaction, long quantity, User user) {
        long difference = itemTransaction.getQuantity() - quantity;
        if (itemTransaction.getQuantity() >= quantity) {
            SavedItemDto savedItemDto = savedItemDtoMapper.toDto(savedItemDao.findSavedItemById(itemTransaction.getSavedItemId()));
            savedItemDao.outComeSavedItem(savedItemDtoMapper.toEntity(savedItemDto), Long.valueOf(itemTransaction.getQuantity()).intValue());
            Transaction transaction = transactionDao.create(ItemTransactionUtil.createTransaction(itemTransaction, user, itemTransaction.getAssociateId(), TransactionType.OUT));
            eventService.create(new Event("Sold  " + itemTransaction.getQuantity() + " " + itemTransaction.getItemDto().getName() +
                " to client " + associateDao.findById(itemTransaction.getAssociateId()).getName(), user.getAccountId(),
                itemTransaction.getSourceWarehouseId(), user.getId(), EventName.ITEM_SHIPPED, transaction.getId().longValue()));
            savedItemDto.setQuantity(Long.valueOf(difference).intValue());
            return savedItemDto;
        }
        eventService.create(new Event("Not enough quantity  " + itemTransaction.getQuantity() + " " + itemTransaction.getItemDto().getName() +
            " in warehouse " + warehouseDao.findById(itemTransaction.getSourceWarehouseId()).getName(), user.getAccountId(),
            itemTransaction.getSourceWarehouseId(), user.getId(), EventName.ITEM_ENDED, null));
        throw new ItemNotEnoughQuantityException("Outcome failed. Can't find needed quantity item in warehouse needed" +
            " quantity of items {warehouse_id = " + itemTransaction.getSourceWarehouseId() + ", quantity = " + itemTransaction.getQuantity() + "}");
    }

}
