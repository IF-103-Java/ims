package com.ita.if103java.ims.service;

import com.ita.if103java.ims.dao.AssociateDao;
import com.ita.if103java.ims.dao.ItemDao;
import com.ita.if103java.ims.dao.SavedItemDao;
import com.ita.if103java.ims.dao.TransactionDao;
import com.ita.if103java.ims.dao.WarehouseDao;
import com.ita.if103java.ims.dto.ItemDto;
import com.ita.if103java.ims.dto.ItemTransactionRequestDto;
import com.ita.if103java.ims.dto.SavedItemDto;
import com.ita.if103java.ims.entity.Associate;
import com.ita.if103java.ims.entity.Event;
import com.ita.if103java.ims.entity.EventName;
import com.ita.if103java.ims.entity.Item;
import com.ita.if103java.ims.entity.SavedItem;
import com.ita.if103java.ims.entity.Transaction;
import com.ita.if103java.ims.entity.TransactionType;
import com.ita.if103java.ims.entity.User;
import com.ita.if103java.ims.entity.Warehouse;
import com.ita.if103java.ims.exception.dao.ItemNotFoundException;
import com.ita.if103java.ims.exception.dao.SavedItemNotFoundException;
import com.ita.if103java.ims.exception.service.ItemDuplicateException;
import com.ita.if103java.ims.exception.service.ItemNotEnoughCapacityInWarehouseException;
import com.ita.if103java.ims.exception.service.ItemNotEnoughQuantityException;
import com.ita.if103java.ims.mapper.dto.ItemDtoMapper;
import com.ita.if103java.ims.mapper.dto.SavedItemDtoMapper;
import com.ita.if103java.ims.security.UserDetailsImpl;
import com.ita.if103java.ims.service.impl.ItemServiceImpl;
import com.ita.if103java.ims.service.impl.SavedItemServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentMatchers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.Spy;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(SpringExtension.class)
public class ItemServiceImplTest {

    private UserDetailsImpl userDetails;
    @Mock
    private ItemDtoMapper itemDtoMapper;

    @Mock
    private SavedItemDtoMapper savedItemDtoMapper;
    @Mock
    private ItemDao itemDao;
    @Mock
    private SavedItemDao savedItemDao;
    @Mock
    private WarehouseDao warehouseDao;
    @Mock
    private TransactionDao transactionDao;
    @Mock
    private EventService eventService;
    @Mock
    private AssociateDao associateDao;
    @Mock
    SavedItemServiceImpl savedItemService;

    @Spy
    @InjectMocks
    ItemServiceImpl itemService;


    private Long accountId;
    private Long userId;


    @BeforeEach
    void setUp() {
        MockitoAnnotations.initMocks(this);

        User user = new User();
        user.setAccountId(2L);
        user.setId(1L);

        userDetails = new UserDetailsImpl(user);
        accountId = user.getAccountId();
        userId = userDetails.getUser().getId();

        ReflectionTestUtils.setField(itemService, "maxWarehouseLoad", "90");
        ReflectionTestUtils.setField(itemService, "minQuantityItemsInWarehouse", "10");
    }

    @Test
    void findById() {
        Item item = getItem();
        ItemDto itemDto = getItemDto();

        when(itemDao.findItemById(108L, 2L)).thenReturn(item);
        when(itemDtoMapper.toDto(item)).thenReturn(itemDto);

        assertEquals(itemService.findById(108L, userDetails), itemDto);
    }

    @Test
    void findSortedItems() {
        List<Item> itemList = getListOfItems();
        List<ItemDto> itemDtoList = getListOfItemDtos();
        Integer expectedCount = 3;

        PageRequest pageable = PageRequest.of(0, 3, Sort.Direction.ASC, "id");
        Page page = new PageImpl(itemDtoList, pageable, expectedCount);

        when(itemDao.getItems(accountId, pageable.getPageSize(), pageable.getOffset(), pageable.getSort()))
            .thenReturn(itemList);
        when(itemDao.countItemsById(accountId)).thenReturn(expectedCount);
        when(itemDtoMapper.toDtoList(itemList)).thenReturn(itemDtoList);

        assertEquals(itemService.findSortedItems(pageable, userDetails), page);
    }

    @Test
    void addItem_successFlowNullItem() {
        Item item = getItem();
        ItemDto itemDto = getItemDto();

        when(itemDao.findItemByName(itemDto.getName(), accountId)).thenReturn(null);

        itemDto.setAccountId(accountId);

        when(itemDtoMapper.toEntity(itemDto)).thenReturn(item);
        when(itemDao.addItem(item)).thenReturn(item);
        when(itemDtoMapper.toDto(item)).thenReturn(itemDto);

        assertEquals(itemService.addItem(itemDto, userDetails), itemDto);
    }

    @Test
    void addItem_successFlowNotNullItemAndNameNotEquals() {
        Item item = getItem();
        ItemDto itemDto = getItemDto();
        item.setName("test");

        when(itemDao.findItemByName(itemDto.getName(), accountId)).thenReturn(item);

        itemDto.setAccountId(accountId);

        when(itemDtoMapper.toEntity(itemDto)).thenReturn(item);
        when(itemDao.addItem(item)).thenReturn(item);
        when(itemDtoMapper.toDto(item)).thenReturn(itemDto);

        assertEquals(itemService.addItem(itemDto, userDetails), itemDto);
    }

    @Test
    void addItem_omittedFlowNotNullItemAndNameEquals() {
        Item item = getItem();
        ItemDto itemDto = getItemDto();

        when(itemDao.findItemByName(itemDto.getName(), accountId)).thenReturn(item);

        ItemDuplicateException itemDuplicateException = assertThrows(ItemDuplicateException.class,
            () -> itemService.addItem(itemDto, userDetails));
        assertEquals("Failed to create item, because exist the same " + getItemDto().toString(),
            itemDuplicateException.getMessage());
    }

    @Test
    void softDelete() {
        when(itemDao.softDeleteItem(108L, userDetails.getUser().getAccountId())).thenReturn(true);

        assertEquals(itemService.softDelete(108L, userDetails), true);
    }


    @Test
    void findItemsByNameQuery() {
        String query = "Fish";

        when(itemDao.findItemsByNameQuery(query, userDetails.getUser().getAccountId())).thenReturn(getListOfItems());
        when(itemDtoMapper.toDtoList(getListOfItems())).thenReturn(getListOfItemDtos());

        assertEquals(itemService.findItemsByNameQuery(query, userDetails), getListOfItemDtos());

    }

    @Test
    void updateItem() {
        Item item = getItem();
        ItemDto itemDto = getItemDto();

        when(itemDtoMapper.toEntity(itemDto)).thenReturn(item);
        when(itemDao.updateItem(item)).thenReturn(item);
        when(itemDtoMapper.toDto(item)).thenReturn(itemDto);

        assertEquals(itemService.updateItem(getItemDto(), userDetails), itemDto);

    }

    @Test
    void findSavedItemById_successFlow() {
        Long savedItemId = getSavedItem().getId();
        SavedItem savedItem = getSavedItem();
        SavedItemDto savedItemDto = getSavedItemDto();

        when(savedItemDao.findSavedItemById(savedItemId)).thenReturn(savedItem);
        when(savedItemDao.findSavedItemById(savedItemId)).thenReturn(savedItem);
        when(savedItemDtoMapper.toDto(savedItem)).thenReturn(savedItemDto);
        when(itemDao.isExistItemById(savedItemDto.getItemId(), userDetails.getUser().getAccountId())).thenReturn(true);

        assertEquals(itemService.findSavedItemById(savedItemId, userDetails), savedItemDto);

    }

    @Test
    void findSavedItemById_omittedItemNotExistByIdFlow() {
        Long savedItemId = getSavedItem().getId();
        SavedItem savedItem = getSavedItem();
        SavedItemDto savedItemDto = getSavedItemDto();

        when(savedItemDao.findSavedItemById(savedItemId)).thenReturn(savedItem);
        when(savedItemDao.findSavedItemById(savedItemId)).thenReturn(savedItem);
        when(savedItemDtoMapper.toDto(savedItem)).thenReturn(savedItemDto);
        when(itemDao.isExistItemById(savedItemDto.getItemId(), userDetails.getUser().getAccountId())).thenReturn(false);

        assertThrows(SavedItemNotFoundException.class, () -> itemService.findSavedItemById(savedItemId, userDetails));

    }

    @Test
    void findByItemId_successFlow() {
        SavedItemDto savedItemDto = new SavedItemDto();
        List<SavedItem> savedItems = new ArrayList<>();
        List<SavedItemDto> savedItemDtos = getListOfSavedItemDtos();

        when(itemDao.isExistItemById(savedItemDto.getItemId(), userDetails.getUser().getAccountId())).thenReturn(true);
        when(savedItemDtoMapper.toDtoList(savedItems)).thenReturn(savedItemDtos);

        assertEquals(itemService.findByItemId(savedItemDto.getItemId(), userDetails), savedItemDtos);
    }

    @Test
    void findByItemId_omittedItemNotExistByIdFlow() {
        SavedItemDto savedItemDto = new SavedItemDto();
        savedItemDto.setItemId(105L);
        savedItemDto.setId(70L);

        when(itemDao.isExistItemById(savedItemDto.getItemId(), userDetails.getUser().getAccountId())).thenReturn(false);

        ItemNotFoundException itemNotFoundException = assertThrows(ItemNotFoundException.class,
            () -> itemService.findByItemId(savedItemDto.getItemId(), userDetails));
        assertEquals("Failed to get item during `select` {item_id = " + savedItemDto.getItemId() + "}",
            itemNotFoundException.getMessage());
    }

    @Test
    void addSavedItem_successFlowExistSameSavedItem() {
        ItemTransactionRequestDto itemTransaction = getItemTransactionRequestDto();
        ItemDto itemDto = getItemDto();

        SavedItemDto savedItemDto = new SavedItemDto();
        Warehouse warehouse = new Warehouse();
        SavedItem savedItem = new SavedItem();
        savedItem.setQuantity(10);

        Optional<SavedItem> optional = Optional.of(savedItem);

        doReturn(itemDto).when(itemService).findById(itemTransaction.getItemId(), userDetails);
        doNothing().when(savedItemService).validateInputs(itemTransaction, itemDto, accountId, TransactionType.IN);
        when(warehouseDao.findById(itemTransaction.getDestinationWarehouseId(), accountId)).thenReturn(warehouse);
        when(savedItemService.isEnoughCapacityInWarehouse(itemTransaction, itemDto, accountId)).thenReturn(true);
        when(savedItemDao.findSavedItemByItemIdAndWarehouseId(itemTransaction.getItemId(),
            itemTransaction.getDestinationWarehouseId())).thenReturn(optional);

        int quantity = Long.valueOf(savedItem.getQuantity() + itemTransaction.getQuantity()).intValue();

        when(savedItemDao.outComeSavedItem(savedItem, quantity)).thenReturn(true);

        savedItemDto.setQuantity(quantity);

        when(savedItemDtoMapper.toDto(savedItem)).thenReturn(savedItemDto);

        assertEquals(itemService.addSavedItem(itemTransaction, userDetails), savedItemDto);

        verify(eventService, never()).create(any(Event.class));
    }

    @Test
    void addSavedItem_successFlowNotExistSameSavedItem() {
        Associate associate = getAssociate();
        ItemTransactionRequestDto itemTransaction = getItemTransactionRequestDto();

        ItemDto itemDto = new ItemDto();
        itemDto.setName("Potato");
        SavedItemDto savedItemDto = getSavedItemDto();

        Warehouse warehouse = new Warehouse();
        warehouse.setName("Section1");
        SavedItem savedItem = new SavedItem();
        savedItem.setQuantity(20);

        Transaction transaction = getTransaction();

        when(itemService.findById(itemTransaction.getItemId(), userDetails)).thenReturn(itemDto);
        doNothing().when(savedItemService).validateInputs(itemTransaction, itemDto, accountId, TransactionType.IN);
        when(warehouseDao.findById(itemTransaction.getDestinationWarehouseId(), accountId)).thenReturn(warehouse);
        when(savedItemService.isEnoughCapacityInWarehouse(itemTransaction, itemDto, accountId)).thenReturn(true);
        when(savedItemDao.findSavedItemByItemIdAndWarehouseId(itemTransaction.getItemId(),
            itemTransaction.getDestinationWarehouseId())).thenReturn(Optional.empty());
        when(savedItemDao.addSavedItem(ArgumentMatchers.<SavedItem>any())).thenReturn(savedItem);
        when(savedItemDtoMapper.toDto(savedItem)).thenReturn(savedItemDto);
        when(transactionDao.create(itemTransaction, userDetails.getUser(), itemTransaction.getAssociateId(),
            TransactionType.IN)).thenReturn(transaction);
        when(transactionDao.create(transaction)).thenReturn(transaction);
        when(associateDao.findById(accountId, itemTransaction.getAssociateId())).thenReturn(associate);

        Event event = new Event("Moved " + itemTransaction.getQuantity() + " " + itemDto.getName() +
            " to warehouse " + warehouse.getName() + " " +
            "from supplier " + associate.getName(),
            accountId,
            itemTransaction.getDestinationWarehouseId(), userDetails.getUser().getId(), EventName.ITEM_CAME,
            transaction.getId().longValue());

        doNothing().when(eventService).create(event);
        when(savedItemService.isLowSpaceInWarehouse(itemTransaction, accountId)).thenReturn(false);

        assertEquals(itemService.addSavedItem(itemTransaction, userDetails), savedItemDto);

        verify(eventService, times(1)).create(event);
        verify(transactionDao, times(1)).create(transaction);
        verify(savedItemService, times(1)).validateInputs(itemTransaction, itemDto, accountId, TransactionType.IN);

    }

    @Test
    void addSavedItem_successFlowLowSpaceInWarehouse() {
        Associate associate = getAssociate();
        ItemTransactionRequestDto itemTransaction = getItemTransactionRequestDto();

        ItemDto itemDto = new ItemDto();
        itemDto.setName("Potato");
        SavedItemDto savedItemDto = getSavedItemDto();
        Warehouse warehouse = getWarehouse();

        SavedItem savedItem = new SavedItem();
        savedItem.setQuantity(10);
        Transaction transaction = getTransaction();

        when(itemService.findById(itemTransaction.getItemId(), userDetails)).thenReturn(itemDto);
        doNothing().when(savedItemService).validateInputs(itemTransaction, itemDto, accountId, TransactionType.IN);
        when(warehouseDao.findById(itemTransaction.getDestinationWarehouseId(), accountId)).thenReturn(warehouse);
        when(savedItemService.isEnoughCapacityInWarehouse(itemTransaction, itemDto, accountId)).thenReturn(true);
        when(savedItemDao.findSavedItemByItemIdAndWarehouseId(itemTransaction.getItemId(),
            itemTransaction.getDestinationWarehouseId())).thenReturn(Optional.empty());
        when(savedItemDao.addSavedItem(ArgumentMatchers.<SavedItem>any())).thenReturn(savedItem);
        when(savedItemDtoMapper.toDto(savedItem)).thenReturn(savedItemDto);
        when(transactionDao.create(itemTransaction, userDetails.getUser(), itemTransaction.getAssociateId(),
            TransactionType.IN)).thenReturn(transaction);
        when(transactionDao.create(transaction)).thenReturn(transaction);
        when(associateDao.findById(accountId, itemTransaction.getAssociateId())).thenReturn(associate);

        Event event = new Event("Moved " + itemTransaction.getQuantity() + " " + itemDto.getName() +
            " to warehouse " + warehouse.getName() + " " +
            "from supplier " + associate.getName(),
            accountId,
            itemTransaction.getDestinationWarehouseId(), userDetails.getUser().getId(), EventName.ITEM_CAME,
            transaction.getId().longValue());

        doNothing().when(eventService).create(event);

        when(savedItemService.isLowSpaceInWarehouse(itemTransaction, accountId)).thenReturn(true);

        Event event2 = new Event("Warehouse is loaded more than " + "90" + "%! Capacity " +
            warehouse.getCapacity() +
            " in Warehouse " + warehouse.getName(),
            accountId,
            warehouse.getId(), userDetails.getUser().getId(),
            EventName.LOW_SPACE_IN_WAREHOUSE, null);

        doNothing().when(eventService).create(event2);

        assertEquals(itemService.addSavedItem(itemTransaction, userDetails), savedItemDto);

        verify(eventService, times(1)).create(event);
        verify(eventService, times(1)).create(event2);
        verify(transactionDao, times(1)).create(transaction);
        verify(savedItemService, times(1)).validateInputs(itemTransaction, itemDto, accountId, TransactionType.IN);
    }

    @Test
    void addSavedItem_omittedFlowNotEnoughCapacityInWarehouse() {
        ItemTransactionRequestDto itemTransaction = getItemTransactionRequestDto();
        ItemDto itemDto = new ItemDto();
        itemDto.setName("Potato");
        Warehouse warehouse = getWarehouse();

        when(itemService.findById(itemTransaction.getItemId(), userDetails)).thenReturn(itemDto);
        doNothing().when(savedItemService).validateInputs(itemTransaction, itemDto, accountId, TransactionType.IN);
        when(warehouseDao.findById(itemTransaction.getDestinationWarehouseId(), accountId)).thenReturn(warehouse);
        when(savedItemService.isEnoughCapacityInWarehouse(itemTransaction, itemDto, accountId)).thenReturn(false);

        Event event = new Event("Not enough capacity! Capacity " +
            warehouse.getCapacity() +
            " in Warehouse " +
            warehouse.getName(),
            accountId,
            warehouse.getId(), userDetails.getUser().getId(), EventName.LOW_SPACE_IN_WAREHOUSE
            , null);

        doNothing().when(eventService).create(event);

        ItemNotEnoughCapacityInWarehouseException exception =
            assertThrows(ItemNotEnoughCapacityInWarehouseException.class,
                () -> itemService.addSavedItem(itemTransaction, userDetails));
        assertEquals("Can't add savedItemDto in warehouse because it " + "doesn't  " +
                "have enough capacity {warehouse_id = " + itemTransaction.getDestinationWarehouseId() + "}",
            exception.getMessage());

        verify(eventService, times(1)).create(event);
        verify(transactionDao, never()).create(ArgumentMatchers.<Transaction>any());
        verify(savedItemService, times(1)).validateInputs(itemTransaction, itemDto, accountId, TransactionType.IN);
    }

    @Test
    void moveItem_successFlow() {
        ItemTransactionRequestDto itemTransaction = getItemTransactionRequestDto();
        ItemDto itemDto = new ItemDto();
        itemDto.setName("Potato");
        Warehouse warehouse = getWarehouse();
        Transaction transaction = getTransaction();

        when(itemService.findById(itemTransaction.getItemId(), userDetails)).thenReturn(itemDto);
        doNothing().when(savedItemService).validateInputs(itemTransaction, itemDto, accountId, TransactionType.MOVE);
        when(warehouseDao.findById(itemTransaction.getDestinationWarehouseId(), accountId)).thenReturn(warehouse);
        when(warehouseDao.findById(itemTransaction.getSourceWarehouseId(), accountId)).thenReturn(warehouse);
        when(savedItemService.isEnoughCapacityInWarehouse(itemTransaction, itemDto, accountId)).thenReturn(true);
        when(savedItemDao.updateSavedItem(itemTransaction.getDestinationWarehouseId(),
            itemTransaction.getSavedItemId())).thenReturn(true);
        when(transactionDao.create(itemTransaction, userDetails.getUser(), itemTransaction.getAssociateId(),
            TransactionType.MOVE)).thenReturn(transaction);
        when(transactionDao.create(transaction)).thenReturn(transaction);

        Event event = new Event("Moved " + itemTransaction.getQuantity() + " " + itemDto.getName() +
            " from warehouse " +
            warehouse.getName() + " to " +
            "warehouse " + warehouse.getName(), accountId,
            itemTransaction.getSourceWarehouseId(), userDetails.getUser().getId(), EventName.ITEM_MOVED,
            transaction.getId().longValue());

        doNothing().when(eventService).create(event);
        when(savedItemService.isLowSpaceInWarehouse(itemTransaction, accountId)).thenReturn(false);

        assertEquals(itemService.moveItem(itemTransaction, userDetails), true);

        verify(eventService, times(1)).create(event);
        verify(transactionDao, times(1)).create(transaction);
        verify(savedItemService, times(1)).validateInputs(itemTransaction, itemDto, accountId, TransactionType.MOVE);
    }

    @Test
    void moveItem_successFlowLowSpaceInWarehouse() {
        ItemTransactionRequestDto itemTransaction = getItemTransactionRequestDto();
        ItemDto itemDto = new ItemDto();
        itemDto.setName("Potato");
        Warehouse warehouse = getWarehouse();
        Transaction transaction = getTransaction();

        when(itemService.findById(itemTransaction.getItemId(), userDetails)).thenReturn(itemDto);
        doNothing().when(savedItemService).validateInputs(itemTransaction, itemDto, accountId, TransactionType.MOVE);
        when(warehouseDao.findById(itemTransaction.getDestinationWarehouseId(), accountId)).thenReturn(warehouse);
        when(warehouseDao.findById(itemTransaction.getSourceWarehouseId(), accountId)).thenReturn(warehouse);
        when(savedItemService.isEnoughCapacityInWarehouse(itemTransaction, itemDto, accountId)).thenReturn(true);
        when(savedItemDao.updateSavedItem(itemTransaction.getDestinationWarehouseId(),
            itemTransaction.getSavedItemId())).thenReturn(true);
        when(transactionDao.create(itemTransaction, userDetails.getUser(), itemTransaction.getAssociateId(),
            TransactionType.MOVE)).thenReturn(transaction);
        when(transactionDao.create(transaction)).thenReturn(transaction);

        Event event = new Event("Moved " + itemTransaction.getQuantity() + " " + itemDto.getName() +
            " from warehouse " +
            warehouse.getName() + " to " +
            "warehouse " + warehouse.getName(), accountId,
            itemTransaction.getSourceWarehouseId(), userId, EventName.ITEM_MOVED,
            transaction.getId().longValue());

        doNothing().when(eventService).create(event);

        when(savedItemService.isLowSpaceInWarehouse(itemTransaction, accountId)).thenReturn(true);

        Event event2 = new Event("Warehouse is loaded more than " + "90" + "%! Capacity " +
            warehouse.getCapacity() + " in Warehouse " + warehouse.getName(), accountId,
            warehouse.getId(), userId,
            EventName.LOW_SPACE_IN_WAREHOUSE, null);

        doNothing().when(eventService).create(event2);

        assertEquals(itemService.moveItem(itemTransaction, userDetails), true);

        verify(eventService, times(1)).create(event);
        verify(eventService, times(1)).create(event2);
        verify(transactionDao, times(1)).create(transaction);
        verify(savedItemService, times(1)).validateInputs(itemTransaction, itemDto, accountId, TransactionType.MOVE);
    }

    @Test
    void moveItem_successFlowNotEnoughCapacityInWarehouse() {
        ItemTransactionRequestDto itemTransaction = getItemTransactionRequestDto();
        ItemDto itemDto = new ItemDto();
        itemDto.setName("Potato");
        Warehouse warehouse = getWarehouse();

        when(itemService.findById(itemTransaction.getItemId(), userDetails)).thenReturn(itemDto);
        doNothing().when(savedItemService).validateInputs(itemTransaction, itemDto, accountId, TransactionType.MOVE);
        when(warehouseDao.findById(itemTransaction.getDestinationWarehouseId(), accountId)).thenReturn(warehouse);
        when(warehouseDao.findById(itemTransaction.getSourceWarehouseId(), accountId)).thenReturn(warehouse);
        when(savedItemService.isEnoughCapacityInWarehouse(itemTransaction, itemDto, accountId)).thenReturn(false);

        Event event = new Event("Not enough capacity! Capacity " + warehouse.getCapacity() + " in warehouse " +
            warehouse.getName(), accountId,
            warehouse.getId(), userId, EventName.LOW_SPACE_IN_WAREHOUSE, null);

        doNothing().when(eventService).create(event);

        ItemNotEnoughCapacityInWarehouseException exception =
            assertThrows(ItemNotEnoughCapacityInWarehouseException.class,
                () -> itemService.moveItem(itemTransaction, userDetails));
        assertEquals("Can't move savedItemDto in warehouse because it " +
                "doesn't " +
                " have enough capacity {warehouse_id = " + itemTransaction.getDestinationWarehouseId() + "}",
            exception.getMessage());

        verify(transactionDao, never()).create(ArgumentMatchers.<Transaction>any());
        verify(eventService, times(1)).create(event);
        verify(savedItemService, times(1)).validateInputs(itemTransaction, itemDto, accountId, TransactionType.MOVE);
    }

    @Test
    void outcomeItem_successFlow() {
        Associate associate = getAssociate();
        ItemTransactionRequestDto itemTransaction = new ItemTransactionRequestDto();
        itemTransaction.setSourceWarehouseId(18L);
        itemTransaction.setItemId(108L);
        itemTransaction.setQuantity(10L);
        itemTransaction.setAssociateId(40L);
        itemTransaction.setSavedItemId(1L);

        ItemDto itemDto = new ItemDto();
        itemDto.setName("Potato");
        SavedItemDto savedItemDto = new SavedItemDto();
        savedItemDto.setId(1L);
        savedItemDto.setItemId(itemTransaction.getItemId());
        savedItemDto.setQuantity(25);
        savedItemDto.setWarehouseId(itemTransaction.getDestinationWarehouseId());

        SavedItem savedItem = new SavedItem();
        SavedItemDto savedItemDto2 = new SavedItemDto();
        savedItemDto2.setId(1L);
        savedItemDto2.setItemId(itemTransaction.getItemId());
        savedItemDto2.setWarehouseId(itemTransaction.getDestinationWarehouseId());
        Transaction transaction = new Transaction();
        transaction.setId(3L);

        when(itemService.findById(itemTransaction.getItemId(), userDetails)).thenReturn(itemDto);
        doNothing().when(savedItemService).validateInputs(itemTransaction, itemDto, accountId, TransactionType.OUT);
        when(savedItemDao.findSavedItemById(itemTransaction.getSavedItemId())).thenReturn(savedItem);
        when(savedItemDtoMapper.toDto(savedItem)).thenReturn(savedItemDto);
        when(savedItemDtoMapper.toEntity(savedItemDto)).thenReturn(savedItem);

        long difference = savedItemDto.getQuantity() - itemTransaction.getQuantity();

        when(savedItemDao.outComeSavedItem(savedItem, Long.valueOf(difference).intValue())).thenReturn(true);
        when(transactionDao.create(itemTransaction,
            userDetails.getUser(), itemTransaction.getAssociateId(), TransactionType.OUT)).thenReturn(transaction);
        when(transactionDao.create(transaction)).thenReturn(transaction);
        when(associateDao.findById(accountId, associate.getId())).thenReturn(associate);

        Event event = new Event("Sold  " + itemTransaction.getQuantity() + " " + itemDto.getName() + " to client " +
            associate.getName(),
            accountId,
            itemTransaction.getSourceWarehouseId(), userId, EventName.ITEM_SHIPPED,
            transaction.getId().longValue());

        doNothing().when(eventService).create(event);

        savedItemDto2.setQuantity(Long.valueOf(difference).intValue());
        SavedItemDto result = itemService.outcomeItem(itemTransaction, userDetails);

        assertEquals(result.getItemId(), savedItemDto.getItemId());
        assertEquals(result.getId(), savedItemDto2.getId());
        assertEquals(result.getWarehouseId(), savedItemDto2.getWarehouseId());
        assertEquals(result.getQuantity(), savedItemDto2.getQuantity());

        verify(eventService, times(1)).create(event);
        verify(transactionDao, times(1)).create(transaction);
        verify(savedItemService, times(1)).validateInputs(itemTransaction, itemDto, accountId, TransactionType.OUT);

    }

    @Test
    void outcomeItem_successFlowOutcomeCompletelyQuantity() {
        Associate associate = getAssociate();
        ItemTransactionRequestDto itemTransaction = new ItemTransactionRequestDto();
        itemTransaction.setSourceWarehouseId(18L);
        itemTransaction.setItemId(108L);
        itemTransaction.setQuantity(10L);
        itemTransaction.setAssociateId(40L);
        itemTransaction.setSavedItemId(1L);

        ItemDto itemDto = new ItemDto();
        itemDto.setName("Potato");
        SavedItemDto savedItemDto = new SavedItemDto();
        savedItemDto.setId(1L);
        savedItemDto.setItemId(itemTransaction.getItemId());
        savedItemDto.setQuantity(10);
        savedItemDto.setWarehouseId(itemTransaction.getDestinationWarehouseId());

        SavedItem savedItem = new SavedItem();
        SavedItemDto savedItemDto2 = new SavedItemDto();
        savedItemDto2.setId(1L);
        savedItemDto2.setItemId(itemTransaction.getItemId());
        savedItemDto2.setWarehouseId(itemTransaction.getDestinationWarehouseId());
        Transaction transaction = getTransaction();

        when(itemService.findById(itemTransaction.getItemId(), userDetails)).thenReturn(itemDto);
        doNothing().when(savedItemService).validateInputs(itemTransaction, itemDto, accountId, TransactionType.OUT);
        when(savedItemDao.findSavedItemById(itemTransaction.getSavedItemId())).thenReturn(savedItem);
        when(savedItemDtoMapper.toDto(savedItem)).thenReturn(savedItemDto);

        long difference = savedItemDto.getQuantity() - itemTransaction.getQuantity();

        when(savedItemDao.deleteSavedItem(itemTransaction.getSavedItemId())).thenReturn(true);
        when(transactionDao.create(itemTransaction,
            userDetails.getUser(), itemTransaction.getAssociateId(), TransactionType.OUT)).thenReturn(transaction);
        when(transactionDao.create(transaction)).thenReturn(transaction);
        when(associateDao.findById(accountId, associate.getId())).thenReturn(associate);

        Event event = new Event("Sold  " + itemTransaction.getQuantity() + " " + itemDto.getName() + " to client " +
            associate.getName(),
            accountId,
            itemTransaction.getSourceWarehouseId(), userId, EventName.ITEM_SHIPPED,
            transaction.getId().longValue());

        doNothing().when(eventService).create(event);

        savedItemDto2.setQuantity(Long.valueOf(difference).intValue());
        SavedItemDto result = itemService.outcomeItem(itemTransaction, userDetails);

        assertEquals(result.getItemId(), savedItemDto.getItemId());
        assertEquals(result.getId(), savedItemDto2.getId());
        assertEquals(result.getWarehouseId(), savedItemDto2.getWarehouseId());
        assertEquals(result.getQuantity(), savedItemDto2.getQuantity());

        verify(savedItemDao, times(1)).deleteSavedItem(itemTransaction.getSavedItemId());
        verify(eventService, times(1)).create(event);
        verify(transactionDao, times(1)).create(transaction);
        verify(savedItemService, times(1)).validateInputs(itemTransaction, itemDto, accountId, TransactionType.OUT);

    }

    @Test
    void outcomeItem_successFlowLowQuantity() {
        Associate associate = getAssociate();
        ItemTransactionRequestDto itemTransaction = new ItemTransactionRequestDto();
        itemTransaction.setSourceWarehouseId(18L);
        itemTransaction.setItemId(108L);
        itemTransaction.setQuantity(10L);
        itemTransaction.setAssociateId(40L);
        itemTransaction.setSavedItemId(1L);

        ItemDto itemDto = new ItemDto();
        itemDto.setName("Potato");
        Warehouse warehouse = getWarehouse();
        SavedItemDto savedItemDto = new SavedItemDto();
        savedItemDto.setId(1L);
        savedItemDto.setItemId(itemTransaction.getItemId());
        savedItemDto.setQuantity(15);
        savedItemDto.setWarehouseId(itemTransaction.getDestinationWarehouseId());

        SavedItem savedItem = new SavedItem();
        SavedItemDto savedItemDto2 = new SavedItemDto();
        savedItemDto2.setId(1L);
        savedItemDto2.setItemId(itemTransaction.getItemId());
        savedItemDto2.setWarehouseId(itemTransaction.getDestinationWarehouseId());
        Transaction transaction = getTransaction();

        when(itemService.findById(itemTransaction.getItemId(), userDetails)).thenReturn(itemDto);
        doNothing().when(savedItemService).validateInputs(itemTransaction, itemDto, accountId, TransactionType.OUT);
        when(savedItemDao.findSavedItemById(itemTransaction.getSavedItemId())).thenReturn(savedItem);
        when(savedItemDtoMapper.toDto(savedItem)).thenReturn(savedItemDto);
        when(savedItemDtoMapper.toEntity(savedItemDto)).thenReturn(savedItem);

        long difference = savedItemDto.getQuantity() - itemTransaction.getQuantity();

        when(savedItemDao.outComeSavedItem(savedItem, Long.valueOf(difference).intValue())).thenReturn(true);
        when(transactionDao.create(itemTransaction,
            userDetails.getUser(), itemTransaction.getAssociateId(), TransactionType.OUT)).thenReturn(transaction);
        when(transactionDao.create(transaction)).thenReturn(transaction);
        when(associateDao.findById(accountId, associate.getId())).thenReturn(associate);

        Event event = new Event("Sold  " + itemTransaction.getQuantity() + " " + itemDto.getName() + " to client " +
            associate.getName(),
            accountId,
            itemTransaction.getSourceWarehouseId(), userId, EventName.ITEM_SHIPPED,
            transaction.getId().longValue());

        doNothing().when(eventService).create(event);
        when(warehouseDao.findById(itemTransaction.getSourceWarehouseId(), accountId)).thenReturn(warehouse);

        Event event2 = new Event("Left less than " + "10" + " items! Quantity" +
            itemTransaction.getQuantity() + " " +
            itemDto.getName() +
            " in warehouse " +
            warehouse.getName(),
            accountId,
            itemTransaction.getSourceWarehouseId(), userId, EventName.ITEM_ENDED, null);

        doNothing().when(eventService).create(event2);

        savedItemDto2.setQuantity(Long.valueOf(difference).intValue());
        SavedItemDto result = itemService.outcomeItem(itemTransaction, userDetails);

        assertEquals(result.getItemId(), savedItemDto.getItemId());
        assertEquals(result.getId(), savedItemDto2.getId());
        assertEquals(result.getWarehouseId(), savedItemDto2.getWarehouseId());
        assertEquals(result.getQuantity(), savedItemDto2.getQuantity());

        verify(eventService, times(1)).create(event);
        verify(eventService, times(1)).create(event2);
        verify(transactionDao, times(1)).create(transaction);
        verify(savedItemService, times(1)).validateInputs(itemTransaction, itemDto, accountId, TransactionType.OUT);

    }

    @Test
    void outcomeItem_omittedFlowNotEnoughQuantity() {
        ItemTransactionRequestDto itemTransaction = getItemTransactionRequestDto();
        ItemDto itemDto = new ItemDto();
        itemDto.setName("Potato");

        Warehouse warehouse = getWarehouse();
        SavedItemDto savedItemDto = getSavedItemDto();
        SavedItem savedItem = new SavedItem();

        when(itemService.findById(itemTransaction.getItemId(), userDetails)).thenReturn(itemDto);
        doNothing().when(savedItemService).validateInputs(itemTransaction, itemDto, accountId, TransactionType.OUT);
        when(savedItemDao.findSavedItemById(itemTransaction.getSavedItemId())).thenReturn(savedItem);
        when(savedItemDtoMapper.toDto(savedItem)).thenReturn(savedItemDto);
        when(warehouseDao.findById(itemTransaction.getSourceWarehouseId(), accountId)).thenReturn(warehouse);

        Event event = new Event(
            "Not enough quantity  " + itemTransaction.getQuantity() + " " + itemDto.getName() +
                " in warehouse " + warehouse.getName(),
            accountId, itemTransaction.getSourceWarehouseId(), userId, EventName.ITEM_ENDED, null);

        doNothing().when(eventService).create(event);

        ItemNotEnoughQuantityException exception = assertThrows(ItemNotEnoughQuantityException.class,
            () -> itemService.outcomeItem(itemTransaction, userDetails));
        assertEquals("Outcome failed. Can't find needed quantity item in warehouse " +
            "needed" +
            " quantity of items {warehouse_id = " + itemTransaction.getSourceWarehouseId() + ", quantity = " +
            itemTransaction.getQuantity() + "}", exception.getMessage());

        verify(eventService, times(1)).create(event);
        verify(savedItemService, times(1)).validateInputs(itemTransaction, itemDto, accountId, TransactionType.OUT);

    }


    private List<ItemDto> getListOfItemDtos() {
        List<ItemDto> items = new ArrayList<>();

        ItemDto trout = new ItemDto();
        trout.setName("Fish-Trout");
        trout.setDescription("fresh trout");
        trout.setAccountId(accountId);
        trout.setUnit("box");
        trout.setActive(true);
        trout.setVolume(4);
        trout.setId(109L);

        items.add(trout);

        ItemDto salmon = new ItemDto();
        salmon.setName("Fish-Salmon");
        salmon.setDescription("fresh salmon");
        salmon.setAccountId(accountId);
        salmon.setUnit("box");
        trout.setActive(true);
        salmon.setVolume(5);
        salmon.setId(110L);

        items.add(salmon);

        ItemDto catfish = new ItemDto();
        catfish.setName("Catfish");
        catfish.setDescription("fresh salmon");
        catfish.setAccountId(accountId);
        catfish.setUnit("box");
        catfish.setVolume(5);
        catfish.setActive(true);
        catfish.setId(111L);
        items.add(catfish);

        return items;
    }

    private List<Item> getListOfItems() {
        List<Item> items = new ArrayList<>();

        Item trout = new Item();
        trout.setName("Fish-Trout");
        trout.setDescription("fresh trout");
        trout.setAccountId(accountId);
        trout.setUnit("box");
        trout.setActive(true);
        trout.setVolume(4);
        trout.setId(109L);

        items.add(trout);

        Item salmon = new Item();
        salmon.setName("Fish-Salmon");
        salmon.setDescription("fresh salmon");
        salmon.setAccountId(accountId);
        salmon.setUnit("box");
        trout.setActive(true);
        salmon.setVolume(5);
        salmon.setId(110L);

        items.add(salmon);

        Item catfish = new Item();
        catfish.setName("Catfish");
        catfish.setDescription("fresh salmon");
        catfish.setAccountId(accountId);
        catfish.setUnit("box");
        catfish.setVolume(5);
        catfish.setActive(true);
        catfish.setId(111L);
        items.add(catfish);

        return items;
    }

    private Item getItem() {
        Item item = new Item();
        item.setName("Green-Apple");
        item.setDescription("Sweet apple");
        item.setUnit("box");
        item.setActive(true);
        item.setVolume(5);
        item.setId(108L);
        item.setAccountId(accountId);
        return item;
    }

    private ItemDto getItemDto() {
        ItemDto itemDto = new ItemDto();
        itemDto.setName("Green-Apple");
        itemDto.setDescription("Sweet apple");
        itemDto.setUnit("box");
        itemDto.setActive(true);
        itemDto.setVolume(5);
        itemDto.setId(108L);
        itemDto.setAccountId(accountId);
        return itemDto;
    }

    private List<SavedItemDto> getListOfSavedItemDtos() {
        List<SavedItemDto> items = new ArrayList<>();

        SavedItemDto trout = new SavedItemDto();
        trout.setId(70L);
        trout.setQuantity(5);
        trout.setItemId(109L);
        trout.setWarehouseId(37L);

        items.add(trout);

        SavedItemDto salmon = new SavedItemDto();
        salmon.setId(71L);
        salmon.setQuantity(5);
        salmon.setItemId(110L);
        salmon.setWarehouseId(38L);

        items.add(salmon);

        SavedItemDto catfish = new SavedItemDto();
        catfish.setId(72L);
        catfish.setQuantity(5);
        catfish.setItemId(111L);
        catfish.setWarehouseId(39L);

        items.add(catfish);
        return items;
    }

    private SavedItem getSavedItem() {
        SavedItem trout = new SavedItem();
        trout.setId(70L);
        trout.setQuantity(5);
        trout.setItemId(109L);
        trout.setWarehouseId(37L);
        return trout;
    }

    private SavedItemDto getSavedItemDto() {
        SavedItemDto trout = new SavedItemDto();
        trout.setId(70L);
        trout.setQuantity(5);
        trout.setItemId(109L);
        trout.setWarehouseId(37L);
        return trout;
    }

    private ItemTransactionRequestDto getItemTransactionRequestDto() {
        ItemTransactionRequestDto itemTransaction = new ItemTransactionRequestDto();
        itemTransaction.setDestinationWarehouseId(19L);
        itemTransaction.setItemId(108L);
        itemTransaction.setQuantity(10L);
        itemTransaction.setSourceWarehouseId(38L);
        itemTransaction.setSavedItemId(50L);
        return itemTransaction;
    }

    private Associate getAssociate() {
        Associate associate = new Associate();
        associate.setId(40L);
        associate.setName("Nazar");
        return associate;
    }

    private Warehouse getWarehouse() {
        Warehouse warehouse = new Warehouse();
        warehouse.setId(1l);
        warehouse.setName("Section1");
        warehouse.setCapacity(10);
        return warehouse;
    }

    private Transaction getTransaction() {
        Transaction transaction = new Transaction();
        transaction.setId(3L);
        return transaction;
    }
}
