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

import com.ita.if103java.ims.entity.Event;
import com.ita.if103java.ims.entity.EventName;
import com.ita.if103java.ims.entity.Item;
import com.ita.if103java.ims.entity.SavedItem;
import com.ita.if103java.ims.entity.Transaction;
import com.ita.if103java.ims.entity.TransactionType;
import com.ita.if103java.ims.entity.Warehouse;
import com.ita.if103java.ims.exception.ItemNotEnoughCapacityInWarehouseException;
import com.ita.if103java.ims.exception.ItemNotEnoughQuantityException;
import com.ita.if103java.ims.exception.ItemNotFoundException;
import com.ita.if103java.ims.exception.SavedItemNotFoundException;
import com.ita.if103java.ims.mapper.ItemDtoMapper;
import com.ita.if103java.ims.mapper.SavedItemDtoMapper;
import com.ita.if103java.ims.mapper.WarehouseDtoMapper;
import com.ita.if103java.ims.security.UserDetailsImpl;
import com.ita.if103java.ims.service.EventService;
import com.ita.if103java.ims.service.ItemService;
import com.ita.if103java.ims.util.ItemTransactionUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;


import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
public class ItemServiceImpl implements ItemService {
    private static final Logger LOGGER = LoggerFactory.getLogger(ItemServiceImpl.class);
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
    public List<ItemDto> findSortedItems(Pageable pageable, UserDetailsImpl user) {
        String sort = pageable.getSort().toString().replaceAll(": ", " ");
        return itemDtoMapper.toDtoList(itemDao.getItems(sort, pageable.getPageSize(), pageable.getOffset(), user.getUser().getAccountId()));
    }

    @Override
    public ItemDto findById(Long id, UserDetailsImpl user) {
        if (itemDao.isExistItemById(id, user.getUser().getAccountId())){
        return itemDtoMapper.toDto(itemDao.findItemById(id));
        } else {
            throw new ItemNotFoundException("Failed to get item during `select` {item_id = " + id + "}");
        }
    }

    @Override
    public SavedItemDto addSavedItem(ItemTransactionRequestDto itemTransaction, UserDetailsImpl user) {
        if (existInAccount(itemTransaction.getItemDto().getId(), itemTransaction.getDestinationWarehouseId(), user.getUser().getAccountId())&& associateDao.findById(itemTransaction.getAssociateId()).getAccountId()==user.getUser().getAccountId()) {
            if (!isLowSpaceInWarehouse(itemTransaction)){
                Event event = new Event("Warehouse is loaded more than 90%! Capacity " + warehouseDao.findById(itemTransaction.getDestinationWarehouseId()).getCapacity() +
                    " in Warehouse " + warehouseDao.findById(itemTransaction.getDestinationWarehouseId()).getName(), user.getUser().getAccountId(),
                    itemTransaction.getDestinationWarehouseId(), user.getUser().getId(), EventName.LOW_SPACE_IN_WAREHOUSE, null);
                LOGGER.error("Warehouse is loaded more than 90 %!", event);
                eventService.create(event);
            if (!isEnoughCapacityInWarehouse(itemTransaction, ItemTransactionRequestDto::getDestinationWarehouseId)){
                eventService.create(new Event("Not enough capacity! Capacity " + warehouseDao.findById(itemTransaction.getDestinationWarehouseId()).getCapacity() +
                    " in Warehouse " + warehouseDao.findById(itemTransaction.getDestinationWarehouseId()).getName(), user.getUser().getAccountId(),
                    itemTransaction.getDestinationWarehouseId(), user.getUser().getId(), EventName.LOW_SPACE_IN_WAREHOUSE, null));
                throw new ItemNotEnoughCapacityInWarehouseException("Can't add savedItemDto in warehouse because it doesn't  " +
                    "have enough capacity {warehouse_id = " + itemTransaction.getDestinationWarehouseId() + "}");
            }
            }
            SavedItem savedItem = new SavedItem(itemTransaction.getItemDto().getId(), itemTransaction.getQuantity().intValue(), itemTransaction.getDestinationWarehouseId());
            SavedItemDto savedItemDto = savedItemDtoMapper.toDto(savedItemDao.addSavedItem(savedItem));
            Transaction transaction = transactionDao.create(ItemTransactionUtil.createTransaction(itemTransaction, user.getUser(), itemTransaction.getAssociateId(), TransactionType.IN));
            eventService.create(new Event("Moved " + itemTransaction.getQuantity() + " " + itemTransaction.getItemDto().getName() +
                " to warehouse " + warehouseDao.findById(itemTransaction.getDestinationWarehouseId()).getName() + " from supplier " + associateDao.findById(itemTransaction.getAssociateId()).getName(), user.getUser().getAccountId(),
                itemTransaction.getDestinationWarehouseId(), user.getUser().getId(), EventName.ITEM_CAME, transaction.getId().longValue()));
            return savedItemDto;
        }
        throw new SavedItemNotFoundException("Failed to get savedItem during `create` {account_id = " + itemTransaction.getItemDto().getAccountId() + "}");
    }

    private boolean isEnoughCapacityInWarehouse(ItemTransactionRequestDto itemTransaction, Function<ItemTransactionRequestDto, Long> function) {
        float volume = getVolumeOfPassSavedItems(itemTransaction) + itemTransaction.getQuantity()*itemTransaction.getItemDto().getVolume();
        return warehouseDao.findById(itemTransaction.getDestinationWarehouseId()).getCapacity() >= volume;
    }

    private float getVolumeOfPassSavedItems(ItemTransactionRequestDto itemTransaction) {
        float volumePassSavedItems = 0;
        Long warehouseId=itemTransaction.getDestinationWarehouseId();

        if (savedItemDao.existSavedItemByWarehouseId(warehouseId)){
        for (SavedItem savedItem : savedItemDao.findSavedItemByWarehouseId(warehouseId)) {
            Item item = itemDao.findItemById(savedItem.getItemId());
            volumePassSavedItems += savedItem.getQuantity() * item.getVolume();
            System.out.println(volumePassSavedItems+item.getName()+item.getVolume()+savedItem.getQuantity());
        }}
        System.out.println(volumePassSavedItems);
        return volumePassSavedItems;
    }


    private boolean isLowSpaceInWarehouse(ItemTransactionRequestDto itemTransaction){
        if(getVolumeOfPassSavedItems(itemTransaction)==0){
            return true;
        } else {
            return getVolumeOfPassSavedItems(itemTransaction)*100/warehouseDao.findById(itemTransaction.getDestinationWarehouseId()).getCapacity()<90.0;
        }

    }

    @Override
    public List<WarehouseDto> findUsefulWarehouses(int volume, int quantity, UserDetailsImpl user) {
        int capacity = volume * quantity;
        List<Warehouse> childWarehouses = new ArrayList<>();
        for (Warehouse warehouse : warehouseDao.findAll()) {
            childWarehouses.addAll(warehouseDao.findChildrenByTopWarehouseID(warehouse.getId()).stream().filter(x -> x.getCapacity() >= capacity).collect(Collectors.toList()));
        }
        return warehouseDtoMapper.toDtoList(childWarehouses);

    }

    @Override
    public ItemDto addItem(ItemDto itemDto, UserDetailsImpl user) {
        itemDto.setAccountId(user.getUser().getAccountId());
        return itemDtoMapper.toDto(itemDao.addItem(itemDtoMapper.toEntity(itemDto)));
    }

    @Override
    public SavedItemDto findSavedItemById(Long id, UserDetailsImpl user) {
        SavedItemDto savedItemDto = savedItemDtoMapper.toDto(savedItemDao.findSavedItemById(id));
         if (itemDao.isExistItemById(savedItemDto.getItemId(), user.getUser().getAccountId())){
            return savedItemDto;
         } else {
             throw new SavedItemNotFoundException("Failed to get savedItem during `findSavedItemById` {account_id = " + user.getUser().getAccountId()+"}");
         }
    }


    @Override
    public boolean softDelete(Long id, UserDetailsImpl user) {
        return itemDao.softDeleteItem(id, user.getUser().getAccountId());
    }

    @Override
    public List<SavedItemDto> findByItemId(Long id, UserDetailsImpl user) {
         if (itemDao.isExistItemById(id, user.getUser().getAccountId())){
            return savedItemDtoMapper.toDtoList(savedItemDao.findSavedItemByItemId(id));
         } else {
             throw new ItemNotFoundException("Failed to get item during `select` {item_id = " + id + "}");
         }
    }

private boolean existInAccount(Long itemId, Long destinationWarehouseId, Long accountId){
     return itemDao.isExistItemById(itemId, accountId)&&warehouseDao.findById(destinationWarehouseId).getAccountID()==accountId;
}
    @Override
    public boolean moveItem(ItemTransactionRequestDto itemTransaction, UserDetailsImpl user) {
        if (existInAccount(itemTransaction.getItemDto().getId(), itemTransaction.getDestinationWarehouseId(), user.getUser().getAccountId())) {
            if (!isLowSpaceInWarehouse(itemTransaction)){
                Event event=new Event("Warehouse is loaded more than 90%! Capacity " + warehouseDao.findById(itemTransaction.getDestinationWarehouseId()).getCapacity() +
                    " in Warehouse " + warehouseDao.findById(itemTransaction.getDestinationWarehouseId()).getName(), user.getUser().getAccountId(),
                    itemTransaction.getDestinationWarehouseId(), user.getUser().getId(), EventName.LOW_SPACE_IN_WAREHOUSE, null);
                LOGGER.error("Warehouse is loaded more than 90 % Capacity", event);
                eventService.create(event);
                if (!isEnoughCapacityInWarehouse(itemTransaction, ItemTransactionRequestDto::getDestinationWarehouseId)){
                    eventService.create(new Event("Not enough capacity! Capacity " + warehouseDao.findById(itemTransaction.getDestinationWarehouseId()).getCapacity() +
                        " in warehouse " + warehouseDao.findById(itemTransaction.getDestinationWarehouseId()).getName(), user.getUser().getAccountId(),
                        itemTransaction.getDestinationWarehouseId(), user.getUser().getId(), EventName.LOW_SPACE_IN_WAREHOUSE, null));
                    throw new ItemNotEnoughCapacityInWarehouseException("Can't move savedItemDto in warehouse because it doesn't " +
                        " have enough capacity {warehouse_id = " + itemTransaction.getDestinationWarehouseId() + "}");
                }}
            boolean isMove = savedItemDao.updateSavedItem(itemTransaction.getDestinationWarehouseId(), itemTransaction.getSourceWarehouseId());
            Transaction transaction = transactionDao.create(ItemTransactionUtil.createTransaction(itemTransaction, user.getUser(), itemTransaction.getAssociateId(), TransactionType.MOVE));
            eventService.create(new Event("Moved " + itemTransaction.getQuantity() + " " + itemTransaction.getItemDto().getName() +
                " from warehouse " + warehouseDao.findById(itemTransaction.getSourceWarehouseId()).getName() + " to warehouse " + warehouseDao.findById(itemTransaction.getDestinationWarehouseId()).getName(), user.getUser().getAccountId(),
                itemTransaction.getSourceWarehouseId(), user.getUser().getId(), EventName.ITEM_MOVED, transaction.getId().longValue()));
            return isMove;
        }
        throw new SavedItemNotFoundException("Failed to get savedItem during `move` {account_id = " + itemTransaction.getItemDto().getAccountId() + " destinationWarehouseId= "+ itemTransaction.getDestinationWarehouseId()+"}");
    }

    @Override
    public SavedItemDto outcomeItem(ItemTransactionRequestDto itemTransaction, UserDetailsImpl user) {
        if (itemDao.isExistItemById(itemTransaction.getItemDto().getId(), user.getUser().getAccountId()) && associateDao.findById(itemTransaction.getAssociateId()).getAccountId()==user.getUser().getAccountId()){
            SavedItemDto savedItemDto = savedItemDtoMapper.toDto(savedItemDao.findSavedItemById(itemTransaction.getSavedItemId()));
        long difference = savedItemDto.getQuantity()-itemTransaction.getQuantity();
            if (savedItemDto.getQuantity()<10) {
                    Event event = new Event("Left less than 10 items! Quantity" + itemTransaction.getQuantity() + " " + itemTransaction.getItemDto().getName() +
                    " in warehouse " + warehouseDao.findById(itemTransaction.getSourceWarehouseId()).getName(), user.getUser().getAccountId(),
                    itemTransaction.getSourceWarehouseId(), user.getUser().getId(), EventName.ITEM_ENDED, null);
                LOGGER.error("Left less than 10 items!", event);
                eventService.create(event);
                if (savedItemDto.getQuantity() < itemTransaction.getQuantity()) {
            eventService.create(new Event("Not enough quantity  " + itemTransaction.getQuantity() + " " + itemTransaction.getItemDto().getName() +
                " in warehouse " + warehouseDao.findById(itemTransaction.getSourceWarehouseId()).getName(), user.getUser().getAccountId(),
                itemTransaction.getSourceWarehouseId(), user.getUser().getId(), EventName.ITEM_ENDED, null));
            throw new ItemNotEnoughQuantityException("Outcome failed. Can't find needed quantity item in warehouse needed" +
                " quantity of items {warehouse_id = " + itemTransaction.getSourceWarehouseId() + ", quantity = " + itemTransaction.getQuantity() + "}");
        }
            }
            savedItemDao.outComeSavedItem(savedItemDtoMapper.toEntity(savedItemDto), Long.valueOf(difference).intValue());
            Transaction transaction = transactionDao.create(ItemTransactionUtil.createTransaction(itemTransaction, user.getUser(), itemTransaction.getAssociateId(), TransactionType.OUT));
            eventService.create(new Event("Sold  " + itemTransaction.getQuantity() + " " + itemTransaction.getItemDto().getName() +
                " to client " + associateDao.findById(itemTransaction.getAssociateId()).getName(), user.getUser().getAccountId(),
                itemTransaction.getSourceWarehouseId(), user.getUser().getId(), EventName.ITEM_SHIPPED, transaction.getId().longValue()));
            savedItemDto.setQuantity(Long.valueOf(difference).intValue());
            return savedItemDto;
        }
        throw new SavedItemNotFoundException("Failed to get savedItem during `outcomeItem` {account_id = " + user.getUser().getAccountId() + " associateId = "+ itemTransaction.getAssociateId()+"}");
    }

}
