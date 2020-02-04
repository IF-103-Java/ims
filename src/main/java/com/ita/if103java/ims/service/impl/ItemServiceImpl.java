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
import com.ita.if103java.ims.entity.Event;
import com.ita.if103java.ims.entity.EventName;
import com.ita.if103java.ims.entity.Item;
import com.ita.if103java.ims.entity.SavedItem;
import com.ita.if103java.ims.entity.Transaction;
import com.ita.if103java.ims.entity.TransactionType;
import com.ita.if103java.ims.exception.dao.ItemNotFoundException;
import com.ita.if103java.ims.exception.dao.SavedItemNotFoundException;
import com.ita.if103java.ims.exception.service.ItemNotEnoughCapacityInWarehouseException;
import com.ita.if103java.ims.exception.service.ItemNotEnoughQuantityException;
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
import java.util.stream.Collectors;

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
        final List<Item> items = itemDao.getItems(accountId, pageable.getPageSize(), pageable.getOffset(), pageable.getSort());
        final Integer count = itemDao.countItemsById(accountId);
        return new PageImpl<>(itemDtoMapper.toDtoList(items), pageable, count);
    }

    @Override
    public ItemDto findById(Long id, UserDetailsImpl user) {
        return itemDtoMapper.toDto(itemDao.findItemById(id, user.getUser().getAccountId()));
    }


    @Transactional
    @Override
    public SavedItemDto addSavedItem(ItemTransactionRequestDto itemTransaction, UserDetailsImpl user) {
        Long accountId = user.getUser().getAccountId();
        Long id = user.getUser().getId();
        savedItemService.validateInputsAdd(itemTransaction, accountId);
        if (savedItemService.isEnoughCapacityInWarehouse(itemTransaction, accountId)) {
            Optional<SavedItem> item = savedItemDao.findSavedItemByItemIdAndWarehouseId(itemTransaction.getItemDto().getId(),
                itemTransaction.getDestinationWarehouseId());
            if (item.isPresent()){
               SavedItem savedItem = item.get();
                int quantity = Long.valueOf(savedItem.getQuantity()+itemTransaction.getQuantity()).intValue();
                savedItem.setQuantity(quantity);
                savedItemDao.outComeSavedItem(savedItem, quantity);
                return savedItemDtoMapper.toDto(savedItem);
            }
                SavedItem savedItem = new SavedItem(itemTransaction.getItemDto().getId(),
                itemTransaction.getQuantity().intValue(), itemTransaction.getDestinationWarehouseId());
            SavedItemDto savedItemDto = savedItemDtoMapper.toDto(savedItemDao.addSavedItem(savedItem));



            Transaction transaction = transactionDao.create(transactionDao.create(itemTransaction,
                user.getUser(), itemTransaction.getAssociateId(), TransactionType.IN));
            eventService.create(new Event("Moved " + itemTransaction.getQuantity() + " " + itemTransaction.getItemDto().getName() +
                " to warehouse " + warehouseDao.findById(itemTransaction.getDestinationWarehouseId(), accountId).getName() + " " +
                "from supplier " + associateDao.findById(accountId, itemTransaction.getAssociateId()).getName(),
                accountId,
                itemTransaction.getDestinationWarehouseId(), id, EventName.ITEM_CAME,
                transaction.getId().longValue()));
            if (savedItemService.isLowSpaceInWarehouse(itemTransaction, accountId)) {
                Event event =
                    new Event("Warehouse is loaded more than " + maxWarehouseLoad + "%! Capacity " +
                        warehouseDao.findById(itemTransaction.getDestinationWarehouseId(), accountId).getCapacity() +
                        " in Warehouse " + warehouseDao.findById(itemTransaction.getDestinationWarehouseId(), accountId).getName(),
                        accountId,
                        itemTransaction.getDestinationWarehouseId(), id,
                        EventName.LOW_SPACE_IN_WAREHOUSE, null);
                LOGGER.info("Warehouse is loaded more than " + maxWarehouseLoad + " %!", event);
                eventService.create(event);
            }
            return savedItemDto;
        } else {
            eventService.create(new Event("Not enough capacity! Capacity " + warehouseDao.findById(itemTransaction.getDestinationWarehouseId(), accountId).getCapacity() +
                " in Warehouse " + warehouseDao.findById(itemTransaction.getDestinationWarehouseId(), accountId).getName(),
                accountId,
                itemTransaction.getDestinationWarehouseId(), id, EventName.LOW_SPACE_IN_WAREHOUSE
                , null));
            throw new ItemNotEnoughCapacityInWarehouseException("Can't add savedItemDto in warehouse because it " +
                "doesn't  " +
                "have enough capacity {warehouse_id = " + itemTransaction.getDestinationWarehouseId() + "}");
        }


    }

    @Override
    public ItemDto addItem(ItemDto itemDto, UserDetailsImpl user) {
        itemDto.setAccountId(user.getUser().getAccountId());
        return itemDtoMapper.toDto(itemDao.addItem(itemDtoMapper.toEntity(itemDto)));
    }

    @Override
    public SavedItemDto findSavedItemById(Long id, UserDetailsImpl user) {
        Long accountId = user.getUser().getAccountId();
        SavedItemDto savedItemDto = savedItemDtoMapper.toDto(savedItemDao.findSavedItemById(id));
        if (itemDao.isExistItemById(savedItemDto.getItemId(), accountId)) {
            return savedItemDto;
        } else {
            throw new SavedItemNotFoundException("Failed to get savedItem during `findSavedItemById` {account_id = " + accountId + "}");
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
        Long id = user.getUser().getId();
        savedItemService.validateInputsMove(itemTransaction, accountId);
        if (savedItemService.isEnoughCapacityInWarehouse(itemTransaction, accountId)) {
            boolean isMove = savedItemDao.updateSavedItem(itemTransaction.getDestinationWarehouseId(),
                itemTransaction.getSavedItemId());
            Transaction transaction = transactionDao.create(transactionDao.create(itemTransaction,
                user.getUser(), itemTransaction.getAssociateId(), TransactionType.MOVE));
            eventService.create(new Event("Moved " + itemTransaction.getQuantity() + " " + itemTransaction.getItemDto().getName() +
                " from warehouse " + warehouseDao.findById(itemTransaction.getSourceWarehouseId(), accountId).getName() + " to " +
                "warehouse " + warehouseDao.findById(itemTransaction.getDestinationWarehouseId(), accountId).getName(),
                accountId,
                itemTransaction.getSourceWarehouseId(), id, EventName.ITEM_MOVED,
                transaction.getId().longValue()));

            if (savedItemService.isLowSpaceInWarehouse(itemTransaction, accountId)) {
                Event event = new Event("Warehouse is loaded more than " + maxWarehouseLoad + "%! Capacity " +
                    warehouseDao.findById(itemTransaction.getDestinationWarehouseId(), accountId).getCapacity() +
                    " in Warehouse " + warehouseDao.findById(itemTransaction.getDestinationWarehouseId(), accountId).getName(),
                    accountId,
                    itemTransaction.getDestinationWarehouseId(), id,
                    EventName.LOW_SPACE_IN_WAREHOUSE, null);
                LOGGER.info("Warehouse is loaded more than " + maxWarehouseLoad + "% Capacity", event);
                eventService.create(event);
            }
            return isMove;
        } else {
            eventService.create(new Event("Not enough capacity! Capacity " + warehouseDao.findById(itemTransaction.getDestinationWarehouseId(), accountId).
                getCapacity() + " in warehouse " + warehouseDao.findById(itemTransaction.getDestinationWarehouseId(), accountId).getName(),
                accountId,
                itemTransaction.getDestinationWarehouseId(), id, EventName.LOW_SPACE_IN_WAREHOUSE
                , null));
            throw new ItemNotEnoughCapacityInWarehouseException("Can't move savedItemDto in warehouse because it " +
                "doesn't " +
                " have enough capacity {warehouse_id = " + itemTransaction.getDestinationWarehouseId() + "}");
        }
    }

    @Transactional
    @Override
    public SavedItemDto outcomeItem(ItemTransactionRequestDto itemTransaction, UserDetailsImpl user) {
        Long accountId = user.getUser().getAccountId();
        Long id = user.getUser().getId();
        savedItemService.validateInputsOut(itemTransaction, user);
        SavedItemDto savedItemDto =
            savedItemDtoMapper.toDto(savedItemDao.findSavedItemById(itemTransaction.getSavedItemId()));
          if (savedItemDto.getQuantity() >= itemTransaction.getQuantity()) {
            long difference = savedItemDto.getQuantity() - itemTransaction.getQuantity();
              if (savedItemDto.getQuantity() == itemTransaction.getQuantity()){
                  savedItemDao.deleteSavedItem(itemTransaction.getSavedItemId());
                  savedItemDto.setQuantity(Long.valueOf(difference).intValue());
                  return savedItemDto;
              }
            savedItemDao.outComeSavedItem(savedItemDtoMapper.toEntity(savedItemDto),
                Long.valueOf(difference).intValue());
            Transaction transaction = transactionDao.create(transactionDao.create(itemTransaction,
                user.getUser(), itemTransaction.getAssociateId(), TransactionType.OUT));
            eventService.create(new Event("Sold  " + itemTransaction.getQuantity() + " " + itemTransaction.getItemDto().getName() +
                " to client " + associateDao.findById(accountId, itemTransaction.getAssociateId()).getName(),
                accountId,
                itemTransaction.getSourceWarehouseId(), id, EventName.ITEM_SHIPPED,
                transaction.getId().longValue()));
            savedItemDto.setQuantity(Long.valueOf(difference).intValue());
            if (savedItemDto.getQuantity() < Integer.parseInt(minQuantityItemsInWarehouse)) {
                Event event =
                    new Event("Left less than " + minQuantityItemsInWarehouse + " items! Quantity" + itemTransaction.getQuantity() + " " +
                        itemTransaction.getItemDto().getName() +
                        " in warehouse " + warehouseDao.findById(itemTransaction.getSourceWarehouseId(), accountId).getName(),
                        accountId,
                        itemTransaction.getSourceWarehouseId(), id, EventName.ITEM_ENDED, null);
                LOGGER.info("Left less than " + minQuantityItemsInWarehouse + " items!", event);
                eventService.create(event);
            }
            return savedItemDto;
        } else {
            eventService.create(new Event("Not enough quantity  " + itemTransaction.getQuantity() + " " + itemTransaction.getItemDto().getName() +
                " in warehouse " + warehouseDao.findById(itemTransaction.getSourceWarehouseId(), accountId).getName(),
                accountId,
                itemTransaction.getSourceWarehouseId(), id, EventName.ITEM_ENDED, null));
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
            .filter(x -> (x.getCapacity()-savedItemService.toVolumeOfPassSavedItems(x.getId(), userId))>=capacity).
                map(x -> new UsefulWarehouseDto(x.getId(), x.getName())).collect(Collectors.toList());

    }
}
