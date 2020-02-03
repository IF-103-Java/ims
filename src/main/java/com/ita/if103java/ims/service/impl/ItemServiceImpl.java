package com.ita.if103java.ims.service.impl;


import com.ita.if103java.ims.dao.AssociateDao;
import com.ita.if103java.ims.dao.ItemDao;
import com.ita.if103java.ims.dao.SavedItemDao;
import com.ita.if103java.ims.dao.TransactionDao;
import com.ita.if103java.ims.dao.WarehouseDao;
import com.ita.if103java.ims.dto.ItemDto;
import com.ita.if103java.ims.dto.ItemTransactionRequestDto;
import com.ita.if103java.ims.dto.SavedItemDto;
import com.ita.if103java.ims.dto.UsefulWarehouseDto;
import com.ita.if103java.ims.entity.*;
import com.ita.if103java.ims.exception.dao.ItemNotFoundException;
import com.ita.if103java.ims.exception.dao.SavedItemNotFoundException;
import com.ita.if103java.ims.exception.service.ItemNotEnoughCapacityInWarehouseException;
import com.ita.if103java.ims.exception.service.ItemNotEnoughQuantityException;
import com.ita.if103java.ims.mapper.dto.ItemDtoMapper;
import com.ita.if103java.ims.mapper.dto.SavedItemDtoMapper;
import com.ita.if103java.ims.security.UserDetailsImpl;
import com.ita.if103java.ims.service.EventService;
import com.ita.if103java.ims.service.ItemService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
public class ItemServiceImpl implements ItemService {
    private static final Logger LOGGER = LoggerFactory.getLogger(ItemServiceImpl.class);
    @Value("${items.maxWarehouseLoad}")
    private String maxWarehouseLoad;
    @Value("${items.minQuantityItemsInWarehouse}")
    private String minQuantityItemsInWarehouse;
    private ItemDtoMapper itemDtoMapper;
    private SavedItemDtoMapper savedItemDtoMapper;
    private ItemDao itemDao;
    private SavedItemDao savedItemDao;
    private WarehouseDao warehouseDao;
    private TransactionDao transactionDao;
    private EventService eventService;
    private AssociateDao associateDao;

    @Autowired
    public ItemServiceImpl(ItemDtoMapper itemDtoMapper, SavedItemDtoMapper savedItemDtoMapper, ItemDao itemDao,
                           SavedItemDao savedItemDao, WarehouseDao warehouseDao, TransactionDao transactionDao,
                           EventService eventService, AssociateDao associateDao) {
        this.itemDtoMapper = itemDtoMapper;
        this.savedItemDtoMapper = savedItemDtoMapper;
        this.itemDao = itemDao;
        this.savedItemDao = savedItemDao;
        this.warehouseDao = warehouseDao;
        this.transactionDao = transactionDao;
        this.eventService = eventService;
        this.associateDao = associateDao;
    }

    private String checkSort(String... sort) {
        String direction = sort[1].equalsIgnoreCase("desc") ? "desc" : "asc";
        return Stream.of("id", "name_item", "unit", "description", "volume").
            filter(x -> x.equalsIgnoreCase(sort[0])).collect(Collectors.joining()) + " " + direction;
    }

    @Override
    public Page<ItemDto> findSortedItems(Pageable pageable, UserDetailsImpl user) {
        List<ItemDto> itemDtos =
            itemDtoMapper.toDtoList(itemDao.getItems(checkSort(pageable.getSort().toString().split(": ")),
                pageable.getPageSize(),
                pageable.getOffset(),
                user.getUser().getAccountId()));
              Integer countItems = itemDao.countItemsById(user.getUser().getAccountId());

        return new PageImpl<>(itemDtos, pageable, countItems);
    }

    @Override
    public ItemDto findById(Long id, UserDetailsImpl user) {
        return itemDtoMapper.toDto(itemDao.findItemById(id, user.getUser().getAccountId()));
    }

    private void validateInputsAdd(ItemTransactionRequestDto itemTransaction, Long accountId) {
        if (!(existInAccount(itemTransaction, accountId) &&
            associateDao.findById(accountId, itemTransaction.getAssociateId()).getAccountId().equals(accountId))) {
            throw new SavedItemNotFoundException("Failed to get savedItem during `create` {account_id = " + itemTransaction.getItemDto().getAccountId() + "}");
        }
    }

    private void validateInputsMove(ItemTransactionRequestDto itemTransaction, Long accountId) {
        if (!(existInAccount(itemTransaction, accountId))) {
            throw new SavedItemNotFoundException("Failed to get savedItem during `move` {account_id = " + itemTransaction.getItemDto().getAccountId() + "}");
        }
    }

    private void validateInputsOut(ItemTransactionRequestDto itemTransaction, UserDetailsImpl user) {
        if (!(itemDao.isExistItemById(itemTransaction.getItemDto().getId(), user.getUser().getAccountId())
            && associateDao.findById(user.getUser().getAccountId(), itemTransaction.getAssociateId()).getAccountId().equals(user.getUser().getAccountId()))) {
            throw new SavedItemNotFoundException("Failed to get savedItem during `outcomeItem` {account_id = " + user.getUser().getAccountId() +
                " associateId = " + itemTransaction.getAssociateId() + "}");
        }
    }

    @Transactional
    @Override
    public SavedItemDto addSavedItem(ItemTransactionRequestDto itemTransaction, UserDetailsImpl user) {
        validateInputsAdd(itemTransaction, user.getUser().getAccountId());
        if (isEnoughCapacityInWarehouse(itemTransaction, user.getUser().getAccountId())) {
            SavedItem savedItem = new SavedItem(itemTransaction.getItemDto().getId(),
                itemTransaction.getQuantity().intValue(), itemTransaction.getDestinationWarehouseId());
            SavedItemDto savedItemDto = savedItemDtoMapper.toDto(savedItemDao.addSavedItem(savedItem));
            Transaction transaction = transactionDao.create(transactionDao.create(itemTransaction,
                user.getUser(), itemTransaction.getAssociateId(), TransactionType.IN));
            eventService.create(new Event("Moved " + itemTransaction.getQuantity() + " " + itemTransaction.getItemDto().getName() +
                " to warehouse " + warehouseDao.findById(itemTransaction.getDestinationWarehouseId(), user.getUser().getAccountId()).getName() + " " +
                "from supplier " + associateDao.findById(user.getUser().getAccountId(), itemTransaction.getAssociateId()).getName(),
                user.getUser().getAccountId(),
                itemTransaction.getDestinationWarehouseId(), user.getUser().getId(), EventName.ITEM_CAME,
                transaction.getId().longValue()));
            if (isLowSpaceInWarehouse(itemTransaction, user.getUser().getAccountId())) {
                Event event =
                    new Event("Warehouse is loaded more than " + maxWarehouseLoad + "%! Capacity " +
                        warehouseDao.findById(itemTransaction.getDestinationWarehouseId(), user.getUser().getAccountId()).getCapacity() +
                        " in Warehouse " + warehouseDao.findById(itemTransaction.getDestinationWarehouseId(), user.getUser().getAccountId()).getName(),
                        user.getUser().getAccountId(),
                        itemTransaction.getDestinationWarehouseId(), user.getUser().getId(),
                        EventName.LOW_SPACE_IN_WAREHOUSE, null);
                LOGGER.error("Warehouse is loaded more than " + maxWarehouseLoad + " %!", event);
                eventService.create(event);
            }
            return savedItemDto;
        } else {
            eventService.create(new Event("Not enough capacity! Capacity " + warehouseDao.findById(itemTransaction.getDestinationWarehouseId(), user.getUser().getAccountId()).getCapacity() +
                " in Warehouse " + warehouseDao.findById(itemTransaction.getDestinationWarehouseId(), user.getUser().getAccountId()).getName(),
                user.getUser().getAccountId(),
                itemTransaction.getDestinationWarehouseId(), user.getUser().getId(), EventName.LOW_SPACE_IN_WAREHOUSE
                , null));
            throw new ItemNotEnoughCapacityInWarehouseException("Can't add savedItemDto in warehouse because it " +
                "doesn't  " +
                "have enough capacity {warehouse_id = " + itemTransaction.getDestinationWarehouseId() + "}");
        }


    }


    private boolean isEnoughCapacityInWarehouse(ItemTransactionRequestDto itemTransaction, Long accountId) {
        float volume =
            toVolumeOfPassSavedItems(itemTransaction, accountId) + itemTransaction.getQuantity() * itemTransaction.getItemDto().getVolume();
        return warehouseDao.findById(itemTransaction.getDestinationWarehouseId(), accountId).getCapacity() >= volume;
    }

    private float toVolumeOfPassSavedItems(ItemTransactionRequestDto itemTransaction, Long accountId) {
        float volumePassSavedItems = 0;
        Long warehouseId = itemTransaction.getDestinationWarehouseId();

        if (savedItemDao.existSavedItemByWarehouseId(warehouseId)) {
            for (SavedItem savedItem : savedItemDao.findSavedItemByWarehouseId(warehouseId)) {
                Item item = itemDao.findItemById(savedItem.getItemId(), accountId);
                volumePassSavedItems += savedItem.getQuantity() * item.getVolume();
            }
        }
        return volumePassSavedItems;
    }
    private float toVolumeOfPassSavedItems(Long warehouseId, Long accountId) {
        float volumePassSavedItems = 0;

        if (savedItemDao.existSavedItemByWarehouseId(warehouseId)) {
            for (SavedItem savedItem : savedItemDao.findSavedItemByWarehouseId(warehouseId)) {
                Item item = itemDao.findItemById(savedItem.getItemId(), accountId);
                volumePassSavedItems += savedItem.getQuantity() * item.getVolume();
            }
        }
        System.out.println("warehouseId "+warehouseId+"volume "+ volumePassSavedItems);
        return volumePassSavedItems;
    }


    private boolean isLowSpaceInWarehouse(ItemTransactionRequestDto itemTransaction, Long accountId) {
        float volume = toVolumeOfPassSavedItems(itemTransaction, accountId);
        if (volume == 0) {
            return true;
        } else {
            return volume * 100 / warehouseDao.findById(itemTransaction.getDestinationWarehouseId(), accountId).getCapacity() > Float.parseFloat(maxWarehouseLoad);
        }

    }

    @Override
    public ItemDto addItem(ItemDto itemDto, UserDetailsImpl user) {
        itemDto.setAccountId(user.getUser().getAccountId());
        return itemDtoMapper.toDto(itemDao.addItem(itemDtoMapper.toEntity(itemDto)));
    }

    @Override
    public SavedItemDto findSavedItemById(Long id, UserDetailsImpl user) {
        SavedItemDto savedItemDto = savedItemDtoMapper.toDto(savedItemDao.findSavedItemById(id));
        if (itemDao.isExistItemById(savedItemDto.getItemId(), user.getUser().getAccountId())) {
            return savedItemDto;
        } else {
            throw new SavedItemNotFoundException("Failed to get savedItem during `findSavedItemById` {account_id = " + user.getUser().getAccountId() + "}");
        }
    }


    @Override
    public boolean softDelete(Long id, UserDetailsImpl user) {
        return itemDao.softDeleteItem(id, user.getUser().getAccountId());
    }

    @Override
    public List<SavedItemDto> findByItemId(Long id, UserDetailsImpl user) {
        if (itemDao.isExistItemById(id, user.getUser().getAccountId())) {
            return savedItemDtoMapper.toDtoList(savedItemDao.findSavedItemByItemId(id));
        } else {
            throw new ItemNotFoundException("Failed to get item during `select` {item_id = " + id + "}");
        }
    }

    private boolean existInAccount(ItemTransactionRequestDto itemTransaction, Long accountId) {
        return itemDao.isExistItemById(itemTransaction.getItemDto().getId(), accountId) &&
            accountId.equals(warehouseDao.findById(itemTransaction.getDestinationWarehouseId(), accountId).getAccountID());
    }

    @Transactional
    @Override
    public boolean moveItem(ItemTransactionRequestDto itemTransaction, UserDetailsImpl user) {
        validateInputsMove(itemTransaction, user.getUser().getAccountId());
        if (isEnoughCapacityInWarehouse(itemTransaction, user.getUser().getAccountId())) {
            boolean isMove = savedItemDao.updateSavedItem(itemTransaction.getDestinationWarehouseId(),
                itemTransaction.getSavedItemId());
            Transaction transaction = transactionDao.create(transactionDao.create(itemTransaction,
                user.getUser(), itemTransaction.getAssociateId(), TransactionType.MOVE));
            eventService.create(new Event("Moved " + itemTransaction.getQuantity() + " " + itemTransaction.getItemDto().getName() +
                " from warehouse " + warehouseDao.findById(itemTransaction.getSourceWarehouseId(), user.getUser().getAccountId()).getName() + " to " +
                "warehouse " + warehouseDao.findById(itemTransaction.getDestinationWarehouseId(), user.getUser().getAccountId()).getName(),
                user.getUser().getAccountId(),
                itemTransaction.getSourceWarehouseId(), user.getUser().getId(), EventName.ITEM_MOVED,
                transaction.getId().longValue()));

            if (isLowSpaceInWarehouse(itemTransaction, user.getUser().getAccountId())) {
                Event event = new Event("Warehouse is loaded more than " + maxWarehouseLoad + "%! Capacity " +
                    warehouseDao.findById(itemTransaction.getDestinationWarehouseId(), user.getUser().getAccountId()).getCapacity() +
                    " in Warehouse " + warehouseDao.findById(itemTransaction.getDestinationWarehouseId(), user.getUser().getAccountId()).getName(),
                    user.getUser().getAccountId(),
                    itemTransaction.getDestinationWarehouseId(), user.getUser().getId(),
                    EventName.LOW_SPACE_IN_WAREHOUSE, null);
                LOGGER.error("Warehouse is loaded more than " + maxWarehouseLoad + "% Capacity", event);
                eventService.create(event);
            }
            return isMove;
        } else {
            eventService.create(new Event("Not enough capacity! Capacity " + warehouseDao.findById(itemTransaction.getDestinationWarehouseId(), user.getUser().getAccountId()).
                getCapacity() + " in warehouse " + warehouseDao.findById(itemTransaction.getDestinationWarehouseId(), user.getUser().getAccountId()).getName(),
                user.getUser().getAccountId(),
                itemTransaction.getDestinationWarehouseId(), user.getUser().getId(), EventName.LOW_SPACE_IN_WAREHOUSE
                , null));
            throw new ItemNotEnoughCapacityInWarehouseException("Can't move savedItemDto in warehouse because it " +
                "doesn't " +
                " have enough capacity {warehouse_id = " + itemTransaction.getDestinationWarehouseId() + "}");
        }
    }

    @Transactional
    @Override
    public SavedItemDto outcomeItem(ItemTransactionRequestDto itemTransaction, UserDetailsImpl user) {
        validateInputsOut(itemTransaction, user);
        SavedItemDto savedItemDto =
            savedItemDtoMapper.toDto(savedItemDao.findSavedItemById(itemTransaction.getSavedItemId()));
        if (savedItemDto.getQuantity() >= itemTransaction.getQuantity()) {
            long difference = savedItemDto.getQuantity() - itemTransaction.getQuantity();
            savedItemDao.outComeSavedItem(savedItemDtoMapper.toEntity(savedItemDto),
                Long.valueOf(difference).intValue());
            Transaction transaction = transactionDao.create(transactionDao.create(itemTransaction,
                user.getUser(), itemTransaction.getAssociateId(), TransactionType.OUT));
            eventService.create(new Event("Sold  " + itemTransaction.getQuantity() + " " + itemTransaction.getItemDto().getName() +
                " to client " + associateDao.findById(user.getUser().getAccountId(), itemTransaction.getAssociateId()).getName(),
                user.getUser().getAccountId(),
                itemTransaction.getSourceWarehouseId(), user.getUser().getId(), EventName.ITEM_SHIPPED,
                transaction.getId().longValue()));
            savedItemDto.setQuantity(Long.valueOf(difference).intValue());
            if (savedItemDto.getQuantity() < Integer.parseInt(minQuantityItemsInWarehouse)) {
                Event event =
                    new Event("Left less than " + minQuantityItemsInWarehouse + " items! Quantity" + itemTransaction.getQuantity() + " " +
                        itemTransaction.getItemDto().getName() +
                        " in warehouse " + warehouseDao.findById(itemTransaction.getSourceWarehouseId(), user.getUser().getAccountId()).getName(),
                        user.getUser().getAccountId(),
                        itemTransaction.getSourceWarehouseId(), user.getUser().getId(), EventName.ITEM_ENDED, null);
                LOGGER.error("Left less than " + minQuantityItemsInWarehouse + " items!", event);
                eventService.create(event);
            }
            return savedItemDto;
        } else {
            eventService.create(new Event("Not enough quantity  " + itemTransaction.getQuantity() + " " + itemTransaction.getItemDto().getName() +
                " in warehouse " + warehouseDao.findById(itemTransaction.getSourceWarehouseId(), user.getUser().getAccountId()).getName(),
                user.getUser().getAccountId(),
                itemTransaction.getSourceWarehouseId(), user.getUser().getId(), EventName.ITEM_ENDED, null));
            throw new ItemNotEnoughQuantityException("Outcome failed. Can't find needed quantity item in warehouse " +
                "needed" +
                " quantity of items {warehouse_id = " + itemTransaction.getSourceWarehouseId() + ", quantity = " + itemTransaction.getQuantity() + "}");
        }


    }

    @Override
    public List<ItemDto> findItemsByNameQuery(String query, UserDetailsImpl user) {
        return itemDtoMapper.toDtoList(itemDao.findItemsByNameQuery(query, user.getUser().getAccountId()));
    }

    @Override
    public ItemDto updateItem(ItemDto itemDto, UserDetailsImpl user) {
        itemDto.setAccountId(user.getUser().getAccountId());
        return itemDtoMapper.toDto(itemDao.updateItem(itemDtoMapper.toEntity(itemDto)));
    }

    @Override
    public List<UsefulWarehouseDto> findUsefulWarehouses(Long capacity, UserDetailsImpl user) {
        Long userId = user.getUser().getAccountId();
        return warehouseDao.findUsefulWarehouses(capacity, userId).stream()
            .filter(x -> (x.getCapacity()-toVolumeOfPassSavedItems(x.getId(), userId))>=capacity).
                map(x -> new UsefulWarehouseDto(x.getId(), x.getName())).collect(Collectors.toList());

    }

}
