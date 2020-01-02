package com.ita.if103java.ims.service.impl;


import com.ita.if103java.ims.dao.ItemDao;
import com.ita.if103java.ims.dao.SavedItemDao;
import com.ita.if103java.ims.dao.TransactionDao;
import com.ita.if103java.ims.dao.WarehouseDao;
import com.ita.if103java.ims.dao.AssociateDao;
import com.ita.if103java.ims.dao.impl.WarehouseDaoImpl;
import com.ita.if103java.ims.dto.ItemDto;
import com.ita.if103java.ims.dto.ItemTransactionRequestDto;
import com.ita.if103java.ims.dto.SavedItemDto;
import com.ita.if103java.ims.dto.WarehouseDto;


import com.ita.if103java.ims.entity.*;
import com.ita.if103java.ims.exception.BaseRuntimeException;
import com.ita.if103java.ims.exception.ItemNotEnoughCapacityInWarehouseException;
import com.ita.if103java.ims.exception.ItemNotEnoughQuantityException;
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
        itemDao.isExistItemById(id, user.getUser().getAccountId());
        return itemDtoMapper.toDto(itemDao.findItemById(id));

    }

    @Override
    public SavedItemDto addSavedItem(ItemTransactionRequestDto itemTransaction, UserDetailsImpl user) {
        if (itemDao.isExistItemById(itemTransaction.getItemDto().getId(), user.getUser().getAccountId()) && isEnoughCapacityInWarehouse(itemTransaction) && isLowSpaceInWarehouse(itemTransaction)) {
            SavedItem savedItem = new SavedItem(itemTransaction.getItemDto().getId(), itemTransaction.getQuantity().intValue(), itemTransaction.getSourceWarehouseId());
            Event event = new Event("Warehouse is loaded more than 90%! Capacity " + warehouseDao.findById(itemTransaction.getDestinationWarehouseId()).getCapacity() +
                " in Warehouse " + warehouseDao.findById(itemTransaction.getDestinationWarehouseId()).getName(), user.getUser().getAccountId(),
                itemTransaction.getDestinationWarehouseId(), user.getUser().getId(), EventName.LOW_SPACE_IN_WAREHOUSE, null);
            LOGGER.error("Warehouse is loaded more than 90 %!", event);
            eventService.create(event);

            SavedItemDto savedItemDto = savedItemDtoMapper.toDto(savedItemDao.addSavedItem(savedItem));
            Transaction transaction = transactionDao.create(ItemTransactionUtil.createTransaction(itemTransaction, user.getUser(), itemTransaction.getAssociateId(), TransactionType.IN));
            eventService.create(new Event("Moved " + itemTransaction.getQuantity() + " " + itemTransaction.getItemDto().getName() +
                " to warehouse " + warehouseDao.findById(itemTransaction.getDestinationWarehouseId()).getName() + " from supplier " + associateDao.findById(itemTransaction.getAssociateId()).getName(), user.getUser().getAccountId(),
                itemTransaction.getDestinationWarehouseId(), user.getUser().getId(), EventName.ITEM_CAME, transaction.getId().longValue()));
            return savedItemDto;
        } else {
        eventService.create(new Event("Not enough capacity! Capacity " + warehouseDao.findById(itemTransaction.getDestinationWarehouseId()).getCapacity() +
            " in Warehouse " + warehouseDao.findById(itemTransaction.getDestinationWarehouseId()).getName(), user.getUser().getAccountId(),
            itemTransaction.getDestinationWarehouseId(), user.getUser().getId(), EventName.LOW_SPACE_IN_WAREHOUSE, null));
        throw new ItemNotEnoughCapacityInWarehouseException("Can't add savedItemDto in warehouse because it doesn't  " +
            "have enough capacity {warehouse_id = " + itemTransaction.getDestinationWarehouseId() + "}");
        }
    }

    private boolean isEnoughCapacityInWarehouse(ItemTransactionRequestDto itemTransaction) {
        System.out.println("isEnoughCapacityInWarehouse");
        float volume = getVolumeOfPassSavedItems(itemTransaction.getSourceWarehouseId()) + itemTransaction.getQuantity()*itemTransaction.getItemDto().getVolume();
        return warehouseDao.findById(itemTransaction.getDestinationWarehouseId()).getCapacity() >= volume;
    }
    private float getVolumeOfPassSavedItems(Long warehouseId){
        float volumePassSavedItems=1;
        if (savedItemDao.existSavedItemByWarehouseId(warehouseId)) {
            System.out.println("getVolumeOfPassSavedItems");
            for (SavedItem savedItem : savedItemDao.findSavedItemByWarehouseId(warehouseId)) {
                Item item = itemDao.findItemById(savedItem.getItemId());
                volumePassSavedItems +=savedItem.getQuantity()*item.getVolume();
            }
        }
        return volumePassSavedItems;
    }
    private boolean isLowSpaceInWarehouse(ItemTransactionRequestDto itemTransaction){
        System.out.println("isLowSpaceInWarehouse");
        return getVolumeOfPassSavedItems(itemTransaction.getSourceWarehouseId())*100/warehouseDao.findById(itemTransaction.getDestinationWarehouseId()).getCapacity()>90.0;
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
        itemDao.addItem(itemDtoMapper.toEntity(itemDto));
        return itemDto;
    }

    @Override
    public SavedItemDto findSavedItemById(Long id, UserDetailsImpl user) {
        SavedItemDto savedItemDto = savedItemDtoMapper.toDto(savedItemDao.findSavedItemById(id));
         itemDao.isExistItemById(savedItemDto.getItemId(), user.getUser().getAccountId());
            return savedItemDto;

    }


    @Override
    public boolean softDelete(Long id, UserDetailsImpl user) {
        return itemDao.softDeleteItem(id, user.getUser().getAccountId());
    }

    @Override
    public List<SavedItemDto> findByItemId(Long id, UserDetailsImpl user) {
         itemDao.isExistItemById(id, user.getUser().getAccountId());
            return savedItemDtoMapper.toDtoList(savedItemDao.findSavedItemByItemId(id));

    }


    @Override
    public boolean moveItem(ItemTransactionRequestDto itemTransaction, UserDetailsImpl user) {
        itemDao.isExistItemById(itemTransaction.getItemDto().getId(), user.getUser().getAccountId());
        if (isEnoughCapacityInWarehouse(itemTransaction)) {
            if (isLowSpaceInWarehouse(itemTransaction)){
                Event event=new Event("Warehouse is loaded more than 90%! Capacity " + warehouseDao.findById(itemTransaction.getDestinationWarehouseId()).getCapacity() +
                    " in Warehouse " + warehouseDao.findById(itemTransaction.getDestinationWarehouseId()).getName(), user.getUser().getAccountId(),
                    itemTransaction.getDestinationWarehouseId(), user.getUser().getId(), EventName.LOW_SPACE_IN_WAREHOUSE, null);
                LOGGER.error("Warehouse is loaded more than 90 % Capacity", event);
                eventService.create(event);
            }
            Transaction transaction = transactionDao.create(ItemTransactionUtil.createTransaction(itemTransaction, user.getUser(), itemTransaction.getAssociateId(), TransactionType.MOVE));
            eventService.create(new Event("Moved " + itemTransaction.getQuantity() + " " + itemTransaction.getItemDto().getName() +
                " from warehouse " + warehouseDao.findById(itemTransaction.getSourceWarehouseId()).getName() + " to warehouse " + warehouseDao.findById(itemTransaction.getDestinationWarehouseId()).getName(), user.getUser().getAccountId(),
                itemTransaction.getSourceWarehouseId(), user.getUser().getId(), EventName.ITEM_MOVED, transaction.getId().longValue()));
            return savedItemDao.updateSavedItem(itemTransaction.getDestinationWarehouseId(), itemTransaction.getSourceWarehouseId());
        }
        eventService.create(new Event("Not enough capacity! Capacity " + warehouseDao.findById(itemTransaction.getDestinationWarehouseId()).getCapacity() +
            " in warehouse " + warehouseDao.findById(itemTransaction.getDestinationWarehouseId()).getName(), user.getUser().getAccountId(),
            itemTransaction.getDestinationWarehouseId(), user.getUser().getId(), EventName.LOW_SPACE_IN_WAREHOUSE, null));
        throw new ItemNotEnoughCapacityInWarehouseException("Can't move savedItemDto in warehouse because it doesn't " +
            " have enough capacity {warehouse_id = " + itemTransaction.getDestinationWarehouseId() + "}");
    }

    @Override
    public SavedItemDto outcomeItem(ItemTransactionRequestDto itemTransaction, UserDetailsImpl user) {
        itemDao.isExistItemById(itemTransaction.getItemDto().getId(), user.getUser().getAccountId());
        SavedItemDto savedItemDto = savedItemDtoMapper.toDto(savedItemDao.findSavedItemById(itemTransaction.getSavedItemId()));
        long difference = savedItemDto.getQuantity()-itemTransaction.getQuantity();
        if (savedItemDto.getQuantity() > itemTransaction.getQuantity()) {
            if (savedItemDto.getQuantity()<10) {
               Event event = new Event("Left less than 10 items! Quantity" + itemTransaction.getQuantity() + " " + itemTransaction.getItemDto().getName() +
                    " in warehouse " + warehouseDao.findById(itemTransaction.getSourceWarehouseId()).getName(), user.getUser().getAccountId(),
                    itemTransaction.getSourceWarehouseId(), user.getUser().getId(), EventName.ITEM_ENDED, null);
               LOGGER.error("Left less than 10 items!", event);
               eventService.create(event);
            }
            savedItemDao.outComeSavedItem(savedItemDtoMapper.toEntity(savedItemDto), Long.valueOf(difference).intValue());
            Transaction transaction = transactionDao.create(ItemTransactionUtil.createTransaction(itemTransaction, user.getUser(), itemTransaction.getAssociateId(), TransactionType.OUT));
            eventService.create(new Event("Sold  " + itemTransaction.getQuantity() + " " + itemTransaction.getItemDto().getName() +
                " to client " + associateDao.findById(itemTransaction.getAssociateId()).getName(), user.getUser().getAccountId(),
                itemTransaction.getSourceWarehouseId(), user.getUser().getId(), EventName.ITEM_SHIPPED, transaction.getId().longValue()));
            savedItemDto.setQuantity(Long.valueOf(difference).intValue());
            return savedItemDto;
        }
        eventService.create(new Event("Not enough quantity  " + itemTransaction.getQuantity() + " " + itemTransaction.getItemDto().getName() +
            " in warehouse " + warehouseDao.findById(itemTransaction.getSourceWarehouseId()).getName(), user.getUser().getAccountId(),
            itemTransaction.getSourceWarehouseId(), user.getUser().getId(), EventName.ITEM_ENDED, null));
        throw new ItemNotEnoughQuantityException("Outcome failed. Can't find needed quantity item in warehouse needed" +
            " quantity of items {warehouse_id = " + itemTransaction.getSourceWarehouseId() + ", quantity = " + itemTransaction.getQuantity() + "}");
    }

}
