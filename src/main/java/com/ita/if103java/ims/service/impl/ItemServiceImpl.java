package com.ita.if103java.ims.service.impl;

import com.ita.if103java.ims.dao.AssociateDao;
import com.ita.if103java.ims.dao.ItemDao;
import com.ita.if103java.ims.dao.SavedItemDao;
import com.ita.if103java.ims.dao.TransactionDao;
import com.ita.if103java.ims.dao.WarehouseDao;
import com.ita.if103java.ims.dto.ItemDto;
import com.ita.if103java.ims.dto.ItemTransactionRequestDto;
import com.ita.if103java.ims.dto.SavedItemDto;
import com.ita.if103java.ims.entity.Event;
import com.ita.if103java.ims.entity.EventName;
import com.ita.if103java.ims.entity.Item;
import com.ita.if103java.ims.entity.SavedItem;
import com.ita.if103java.ims.entity.Transaction;
import com.ita.if103java.ims.entity.TransactionType;
import com.ita.if103java.ims.entity.Warehouse;
import com.ita.if103java.ims.exception.BaseRuntimeException;
import com.ita.if103java.ims.exception.dao.ItemNotFoundException;
import com.ita.if103java.ims.exception.dao.SavedItemNotFoundException;
import com.ita.if103java.ims.exception.service.ItemDuplicateException;
import com.ita.if103java.ims.exception.service.ItemNotEnoughCapacityInWarehouseException;
import com.ita.if103java.ims.exception.service.ItemNotEnoughQuantityException;
import com.ita.if103java.ims.exception.service.ItemValidateInputException;
import com.ita.if103java.ims.mapper.dto.ItemDtoMapper;
import com.ita.if103java.ims.mapper.dto.SavedItemDtoMapper;
import com.ita.if103java.ims.security.UserDetailsImpl;
import com.ita.if103java.ims.service.EventService;
import com.ita.if103java.ims.service.ItemService;
import com.ita.if103java.ims.service.SavedItemService;
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
import java.util.Optional;

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
    private SavedItemService savedItemService;

    @Autowired
    public ItemServiceImpl(ItemDtoMapper itemDtoMapper, SavedItemDtoMapper savedItemDtoMapper, ItemDao itemDao,
        SavedItemDao savedItemDao, WarehouseDao warehouseDao, TransactionDao transactionDao,
        EventService eventService, AssociateDao associateDao, SavedItemService savedItemService) {
        this.itemDtoMapper = itemDtoMapper;
        this.savedItemDtoMapper = savedItemDtoMapper;
        this.itemDao = itemDao;
        this.savedItemDao = savedItemDao;
        this.warehouseDao = warehouseDao;
        this.transactionDao = transactionDao;
        this.eventService = eventService;
        this.associateDao = associateDao;
        this.savedItemService = savedItemService;
    }

    @Override
    public Page<ItemDto> findSortedItems(Pageable pageable, UserDetailsImpl user) {
        final Long accountId = user.getUser().getAccountId();
        final List<Item> items = itemDao.getItems(accountId, pageable.getPageSize(), pageable.getOffset(),
            pageable.getSort());
        final Integer count = itemDao.countItemsById(accountId);
        return new PageImpl<>(itemDtoMapper.toDtoList(items), pageable, count);
    }

    @Override
    public ItemDto findById(Long id, UserDetailsImpl user) {
        Long accountId = user.getUser().getAccountId();
        return itemDtoMapper.toDto(itemDao.findItemById(id, accountId));
    }


    @Transactional
    @Override
    public SavedItemDto addSavedItem(ItemTransactionRequestDto itemTransaction, UserDetailsImpl user) {
        Long accountId = user.getUser().getAccountId();
        Long userId = user.getUser().getId();
        ItemDto itemDto = findById(itemTransaction.getItemId(), user);
        savedItemService.validateInputs(itemTransaction, itemDto, accountId, TransactionType.IN);
        Warehouse warehouse = warehouseDao.findById(itemTransaction.getDestinationWarehouseId(), accountId);
        if (savedItemService.isEnoughCapacityInWarehouse(itemTransaction, itemDto, accountId)) {
            Optional<SavedItem> item =
                savedItemDao.findSavedItemByItemIdAndWarehouseId(itemTransaction.getItemId(),
                    itemTransaction.getDestinationWarehouseId());
            if (item.isPresent()) {
                SavedItem savedItem = item.get();
                int quantity = Long.valueOf(savedItem.getQuantity() + itemTransaction.getQuantity()).intValue();
                savedItem.setQuantity(quantity);
                savedItemDao.outComeSavedItem(savedItem, quantity);
                return savedItemDtoMapper.toDto(savedItem);
            }
            SavedItem savedItem = new SavedItem(itemTransaction.getItemId(),
                itemTransaction.getQuantity().intValue(), itemTransaction.getDestinationWarehouseId());
            SavedItemDto savedItemDto = savedItemDtoMapper.toDto(savedItemDao.addSavedItem(savedItem));


            Transaction transaction = transactionDao.create(transactionDao.create(itemTransaction,
                user.getUser(), itemTransaction.getAssociateId(), TransactionType.IN));
            eventService.create(createAddEvent(itemTransaction, warehouse, itemDto, accountId, transaction, userId));
            if (savedItemService.isLowSpaceInWarehouse(itemTransaction, accountId)) {

                Event event = createAddEventIfLowSpaceInWarehouse(warehouse, accountId, userId);
                LOGGER.info("Warehouse is loaded more than " + maxWarehouseLoad + " %!", event);
                eventService.create(event);
            }
            return savedItemDto;
        } else {
            eventService.create(createAddEventIfNotEnoughCapacityInWarehouse(warehouse, accountId, userId));
            throw new ItemNotEnoughCapacityInWarehouseException("Can't add savedItemDto in warehouse because it " +
                "doesn't  " +
                "have enough capacity {warehouse_id = " + itemTransaction.getDestinationWarehouseId() + "}");
        }
    }


    @Override
    public ItemDto addItem(ItemDto itemDto, UserDetailsImpl user) {
        Long accountId = user.getUser().getAccountId();
        try {
            if (itemDto.equals(itemDtoMapper.toDto(itemDao.findItemByName(itemDto.getName(), accountId)))) {
                throw new ItemDuplicateException("Failed to create item, because exist the same " + itemDto.toString());
            }
            itemDto.setAccountId(accountId);
            return itemDtoMapper.toDto(itemDao.addItem(itemDtoMapper.toEntity(itemDto)));
        } catch (BaseRuntimeException e) {
            throw new ItemValidateInputException(e.getMessage());
        }
    }

    @Override
    public SavedItemDto findSavedItemById(Long id, UserDetailsImpl user) {
        Long accountId = user.getUser().getAccountId();
        SavedItemDto savedItemDto = savedItemDtoMapper.toDto(savedItemDao.findSavedItemById(id));
        if (itemDao.isExistItemById(savedItemDto.getItemId(), accountId)) {
            return savedItemDto;
        } else {
            throw new SavedItemNotFoundException(
                "Failed to get savedItem during `findSavedItemById` {account_id = " + accountId + "}");
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

    @Transactional
    @Override
    public boolean moveItem(ItemTransactionRequestDto itemTransaction, UserDetailsImpl user) {
        Long accountId = user.getUser().getAccountId();
        Long userId = user.getUser().getId();
        ItemDto itemDto = findById(itemTransaction.getItemId(), user);
        savedItemService.validateInputs(itemTransaction, itemDto, accountId, TransactionType.MOVE);
        Warehouse warehouse = warehouseDao.findById(itemTransaction.getDestinationWarehouseId(), accountId);
        if (savedItemService.isEnoughCapacityInWarehouse(itemTransaction, itemDto, accountId)) {
            boolean isMove = savedItemDao.updateSavedItem(itemTransaction.getDestinationWarehouseId(),
                itemTransaction.getSavedItemId());
            Transaction transaction = transactionDao.create(transactionDao.create(itemTransaction,
                user.getUser(), itemTransaction.getAssociateId(), TransactionType.MOVE));
            eventService.create(createMoveEvent(itemTransaction, warehouse, itemDto, accountId, transaction, userId));
            if (savedItemService.isLowSpaceInWarehouse(itemTransaction, accountId)) {
                Event event = createMoveEventIfLowSpaceInWarehouse(warehouse, accountId, userId);
                LOGGER.info("Warehouse is loaded more than " + maxWarehouseLoad + "% Capacity", event);
                eventService.create(event);
            }
            return isMove;
        } else {
            eventService.create(createMoveEventIfNotEnoughCapacityInWarehouse(warehouse, accountId, userId));
            throw new ItemNotEnoughCapacityInWarehouseException("Can't move savedItemDto in warehouse because it " +
                "doesn't " +
                " have enough capacity {warehouse_id = " + itemTransaction.getDestinationWarehouseId() + "}");
        }
    }

    @Transactional
    @Override
    public SavedItemDto outcomeItem(ItemTransactionRequestDto itemTransaction, UserDetailsImpl user) {
        Long accountId = user.getUser().getAccountId();
        Long userId = user.getUser().getId();
        ItemDto itemDto = findById(itemTransaction.getItemId(), user);
        savedItemService.validateInputs(itemTransaction, itemDto, accountId, TransactionType.OUT);
        SavedItemDto savedItemDto =
            savedItemDtoMapper.toDto(savedItemDao.findSavedItemById(itemTransaction.getSavedItemId()));
        if (savedItemDto.getQuantity() >= itemTransaction.getQuantity()) {
            long difference = savedItemDto.getQuantity() - itemTransaction.getQuantity();
            if (savedItemDto.getQuantity() == itemTransaction.getQuantity()) {
                savedItemDao.deleteSavedItem(itemTransaction.getSavedItemId());
                savedItemDto.setQuantity(Long.valueOf(difference).intValue());
                return savedItemDto;
            }
            savedItemDao.outComeSavedItem(savedItemDtoMapper.toEntity(savedItemDto),
                Long.valueOf(difference).intValue());
            Transaction transaction = transactionDao.create(transactionDao.create(itemTransaction,
                user.getUser(), itemTransaction.getAssociateId(), TransactionType.OUT));
            eventService.create(createOutEvent(itemTransaction, itemDto, accountId, transaction, userId));
            savedItemDto.setQuantity(Long.valueOf(difference).intValue());
            if (savedItemDto.getQuantity() < Integer.parseInt(minQuantityItemsInWarehouse)) {
                Event event = createOutEventIfMinQuantityItemsInWarehouse(itemTransaction, itemDto, accountId, userId);
                LOGGER.info("Left less than " + minQuantityItemsInWarehouse + " items!", event);
                eventService.create(event);
            }
            return savedItemDto;
        } else {
            eventService.create(createOutEventIfNotEnoughQuantityItemsInWarehouse(itemTransaction, itemDto, accountId,
                userId));
            throw new ItemNotEnoughQuantityException("Outcome failed. Can't find needed quantity item in warehouse " +
                "needed" +
                " quantity of items {warehouse_id = " + itemTransaction.getSourceWarehouseId() + ", quantity = " +
                itemTransaction.getQuantity() + "}");
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

    private Event createAddEvent(ItemTransactionRequestDto itemTransaction, Warehouse warehouse, ItemDto itemDto,
        Long accountId,
        Transaction transaction,
        Long userId) {
        return new Event("Moved " + itemTransaction.getQuantity() + " " + itemDto.getName() +
            " to warehouse " + warehouse.getName() + " " +
            "from supplier " + associateDao.findById(accountId, itemTransaction.getAssociateId()).getName(),
            accountId,
            itemTransaction.getDestinationWarehouseId(), userId, EventName.ITEM_CAME,
            transaction.getId().longValue());
    }

    private Event createAddEventIfLowSpaceInWarehouse(Warehouse warehouse, Long accountId, Long userId) {
        return new Event("Warehouse is loaded more than " + maxWarehouseLoad + "%! Capacity " +
            warehouse.getCapacity() +
            " in Warehouse " + warehouse.getName(),
            accountId,
            warehouse.getId(), userId,
            EventName.LOW_SPACE_IN_WAREHOUSE, null);
    }

    private Event createAddEventIfNotEnoughCapacityInWarehouse(Warehouse warehouse, Long accountId, Long userId) {
        return new Event("Not enough capacity! Capacity " +
            warehouse.getCapacity() +
            " in Warehouse " +
            warehouse.getName(),
            accountId,
            warehouse.getId(), userId, EventName.LOW_SPACE_IN_WAREHOUSE
            , null);
    }

    private Event createMoveEvent(ItemTransactionRequestDto itemTransaction, Warehouse warehouse, ItemDto itemDto,
        Long accountId,
        Transaction transaction,
        Long userId) {
        return new Event("Moved " + itemTransaction.getQuantity() + " " + itemDto.getName() +
            " from warehouse " +
            warehouseDao.findById(itemTransaction.getSourceWarehouseId(), accountId).getName() + " to " +
            "warehouse " + warehouse.getName(), accountId,
            itemTransaction.getSourceWarehouseId(), userId, EventName.ITEM_MOVED,
            transaction.getId().longValue());
    }

    private Event createMoveEventIfLowSpaceInWarehouse(Warehouse warehouse, Long accountId, Long userId) {
        return new Event("Warehouse is loaded more than " + maxWarehouseLoad + "%! Capacity " +
            warehouse.getCapacity() + " in Warehouse " + warehouse.getName(), accountId,
            warehouse.getId(), userId,
            EventName.LOW_SPACE_IN_WAREHOUSE, null);
    }

    private Event createMoveEventIfNotEnoughCapacityInWarehouse(Warehouse warehouse, Long accountId, Long userId) {
        return new Event("Not enough capacity! Capacity " + warehouse.getCapacity() + " in warehouse " +
            warehouse.getName(), accountId,
            warehouse.getId(), userId, EventName.LOW_SPACE_IN_WAREHOUSE, null);
    }

    private Event createOutEvent(ItemTransactionRequestDto itemTransaction, ItemDto itemDto, Long accountId,
        Transaction transaction,
        Long userId) {
        return new Event("Sold  " + itemTransaction.getQuantity() + " " + itemDto.getName() + " to client " +
            associateDao.findById(accountId, itemTransaction.getAssociateId()).getName(),
            accountId,
            itemTransaction.getSourceWarehouseId(), userId, EventName.ITEM_SHIPPED,
            transaction.getId().longValue());
    }

    private Event createOutEventIfMinQuantityItemsInWarehouse(ItemTransactionRequestDto itemTransaction,
        ItemDto itemDto,
        Long accountId, Long userId) {
        return new Event("Left less than " + minQuantityItemsInWarehouse + " items! Quantity" +
            itemTransaction.getQuantity() + " " +
            itemDto.getName() +
            " in warehouse " +
            warehouseDao.findById(itemTransaction.getSourceWarehouseId(), accountId).getName(),
            accountId,
            itemTransaction.getSourceWarehouseId(), userId, EventName.ITEM_ENDED, null);
    }

    private Event createOutEventIfNotEnoughQuantityItemsInWarehouse(ItemTransactionRequestDto itemTransaction,
        ItemDto itemDto, Long accountId, Long userId) {
        return new Event(
            "Not enough quantity  " + itemTransaction.getQuantity() + " " + itemDto.getName() +
                " in warehouse " + warehouseDao.findById(itemTransaction.getSourceWarehouseId(), accountId).getName(),
            accountId, itemTransaction.getSourceWarehouseId(), userId, EventName.ITEM_ENDED, null);
    }
}
