package com.ita.if103java.ims.service;

import com.ita.if103java.ims.dao.AssociateDao;
import com.ita.if103java.ims.dao.ItemDao;
import com.ita.if103java.ims.dao.SavedItemDao;
import com.ita.if103java.ims.dao.WarehouseDao;
import com.ita.if103java.ims.dto.ItemDto;
import com.ita.if103java.ims.dto.ItemTransactionRequestDto;
import com.ita.if103java.ims.entity.Associate;
import com.ita.if103java.ims.entity.AssociateType;
import com.ita.if103java.ims.entity.Item;
import com.ita.if103java.ims.entity.SavedItem;
import com.ita.if103java.ims.entity.TransactionType;
import com.ita.if103java.ims.entity.Warehouse;
import com.ita.if103java.ims.exception.service.SavedItemValidateInputException;
import com.ita.if103java.ims.service.impl.SavedItemServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.Spy;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.when;

@ExtendWith(SpringExtension.class)
public class SavedItemServiceImplTest {
    @Mock
    private ItemDao itemDao;
    @Mock
    private SavedItemDao savedItemDao;
    @Mock
    private WarehouseDao warehouseDao;
    @Mock
    private AssociateDao associateDao;
    @Spy
    @InjectMocks
    SavedItemServiceImpl savedItemService;
    private Long accountId = 2L;
    private Long warehouseId = 37L;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.initMocks(this);
        ReflectionTestUtils.setField(savedItemService, "maxWarehouseLoad", "90");
    }

    @Test
    void validateInputs_Add_successFlow() {
        ItemTransactionRequestDto requestDto = new ItemTransactionRequestDto();
        requestDto.setAssociateId(40L);
        requestDto.setDestinationWarehouseId(37L);
        ItemDto itemDto = new ItemDto();
        itemDto.setVolume(3);
        Warehouse warehouse = new Warehouse();
        Associate associate = new Associate();
        associate.setType(AssociateType.SUPPLIER);

        when(warehouseDao.findById(requestDto.getDestinationWarehouseId(), accountId)).thenReturn(warehouse);
        when(associateDao.findById(accountId, requestDto.getAssociateId())).thenReturn(associate);
        doReturn(true).when(savedItemService).existInAccount(requestDto, accountId);
        assertDoesNotThrow(() -> savedItemService.validateInputs(requestDto, itemDto, accountId, TransactionType.IN));
    }


    @Test
    void validateInputs_Add_omittedFlowVolumeZeroOrLess() {
        ItemTransactionRequestDto requestDto = new ItemTransactionRequestDto();
        ItemDto itemDto = new ItemDto();
        itemDto.setVolume(-3);

        SavedItemValidateInputException exception = assertThrows(SavedItemValidateInputException.class,
            () -> savedItemService.validateInputs(requestDto, itemDto, accountId, TransactionType.IN));
        assertEquals("Failed to get savedItem during `add`" + accountId + "}", exception.getMessage());
    }

    @Test
    void validateInputs_Add_omittedFlowWarehouseNotFound() {
        ItemTransactionRequestDto requestDto = new ItemTransactionRequestDto();
        requestDto.setDestinationWarehouseId(37L);
        ItemDto itemDto = new ItemDto();
        itemDto.setVolume(3);

        when(warehouseDao.findById(requestDto.getDestinationWarehouseId(), accountId)).thenReturn(null);
        SavedItemValidateInputException exception = assertThrows(SavedItemValidateInputException.class,
            () -> savedItemService.validateInputs(requestDto, itemDto, accountId, TransactionType.IN));
        assertEquals("Failed to get savedItem during `add`" + accountId + "}", exception.getMessage());
    }

    @Test
    void validateInputs_Add_omittedFlowNotValidAssociateType() {
        ItemTransactionRequestDto requestDto = new ItemTransactionRequestDto();
        requestDto.setAssociateId(40L);
        requestDto.setDestinationWarehouseId(37L);
        ItemDto itemDto = new ItemDto();
        itemDto.setVolume(3);
        Warehouse warehouse = new Warehouse();
        Associate associate = new Associate();
        associate.setType(AssociateType.CLIENT);

        when(warehouseDao.findById(requestDto.getDestinationWarehouseId(), accountId)).thenReturn(warehouse);
        when(associateDao.findById(accountId, requestDto.getAssociateId())).thenReturn(associate);
        SavedItemValidateInputException exception = assertThrows(SavedItemValidateInputException.class,
            () -> savedItemService.validateInputs(requestDto, itemDto, accountId, TransactionType.IN));
        assertEquals("Failed to get savedItem during `add`" + accountId + "}", exception.getMessage());
    }

    @Test
    void validateInputs_Add_omittedFlowNotExistInAccount() {
        ItemTransactionRequestDto requestDto = new ItemTransactionRequestDto();
        requestDto.setAssociateId(40L);
        requestDto.setDestinationWarehouseId(37L);
        ItemDto itemDto = new ItemDto();
        itemDto.setVolume(3);
        Warehouse warehouse = new Warehouse();
        Associate associate = new Associate();
        associate.setType(AssociateType.SUPPLIER);

        when(warehouseDao.findById(requestDto.getDestinationWarehouseId(), accountId)).thenReturn(warehouse);
        when(associateDao.findById(accountId, requestDto.getAssociateId())).thenReturn(associate);
        doReturn(false).when(savedItemService).existInAccount(requestDto, accountId);
        SavedItemValidateInputException exception = assertThrows(SavedItemValidateInputException.class,
            () -> savedItemService.validateInputs(requestDto, itemDto, accountId, TransactionType.IN));
        assertEquals("Failed to get savedItem during `add`" + accountId + "}", exception.getMessage());
    }

    @Test
    void validateInputs_Move_successFlow() {
        ItemTransactionRequestDto requestDto = new ItemTransactionRequestDto();
        requestDto.setDestinationWarehouseId(37L);
        ItemDto itemDto = new ItemDto();
        itemDto.setVolume(3);
        Warehouse warehouse = new Warehouse();
        warehouse.setBottom(true);

        when(warehouseDao.findById(requestDto.getDestinationWarehouseId(), accountId)).thenReturn(warehouse);
        doReturn(true).when(savedItemService).existInAccount(requestDto, accountId);
        assertDoesNotThrow(() -> savedItemService.validateInputs(requestDto, itemDto, accountId, TransactionType.MOVE));
    }

    @Test
    void validateInputs_Move_omittedFlowVolumeZeroOrLess() {
        ItemTransactionRequestDto requestDto = new ItemTransactionRequestDto();
        ItemDto itemDto = new ItemDto();
        itemDto.setVolume(-3);

        SavedItemValidateInputException exception = assertThrows(SavedItemValidateInputException.class,
            () -> savedItemService.validateInputs(requestDto, itemDto, accountId, TransactionType.MOVE));
        assertEquals("Failed to get savedItem during `move` {account_id = " + accountId + "}", exception.getMessage());
    }

    @Test
    void validateInputs_Move_omittedFlowWarehouseNotBottom() {
        ItemTransactionRequestDto requestDto = new ItemTransactionRequestDto();
        ItemDto itemDto = new ItemDto();
        itemDto.setVolume(3);
        Warehouse warehouse = new Warehouse();

        when(warehouseDao.findById(requestDto.getDestinationWarehouseId(), accountId)).thenReturn(warehouse);
        SavedItemValidateInputException exception = assertThrows(SavedItemValidateInputException.class,
            () -> savedItemService.validateInputs(requestDto, itemDto, accountId, TransactionType.MOVE));
        assertEquals("Failed to get savedItem during `move` {account_id = " + accountId + "}", exception.getMessage());
    }

    @Test
    void validateInputs_Move_omittedFlowNotExistInAccount() {
        ItemTransactionRequestDto requestDto = new ItemTransactionRequestDto();
        requestDto.setDestinationWarehouseId(37L);
        ItemDto itemDto = new ItemDto();
        itemDto.setVolume(3);
        Warehouse warehouse = new Warehouse();
        warehouse.setBottom(true);

        when(warehouseDao.findById(requestDto.getDestinationWarehouseId(), accountId)).thenReturn(warehouse);
        doReturn(false).when(savedItemService).existInAccount(requestDto, accountId);
        SavedItemValidateInputException exception = assertThrows(SavedItemValidateInputException.class,
            () -> savedItemService.validateInputs(requestDto, itemDto, accountId, TransactionType.MOVE));
        assertEquals("Failed to get savedItem during `move` {account_id = " + accountId + "}", exception.getMessage());
    }

    @Test
    void validateInputs_Out_successFlow() {
        ItemTransactionRequestDto requestDto = new ItemTransactionRequestDto();
        requestDto.setAssociateId(40L);
        ItemDto itemDto = new ItemDto();
        Associate associate = new Associate();
        associate.setType(AssociateType.CLIENT);

        when(associateDao.findById(accountId, requestDto.getAssociateId())).thenReturn(associate);
        when(itemDao.isExistItemById(requestDto.getItemId(), accountId)).thenReturn(true);
        assertDoesNotThrow(() -> savedItemService.validateInputs(requestDto, itemDto, accountId, TransactionType.OUT));
    }

    @Test
    void validateInputs_Out_omittedFlowNotValidAssociateType() {
        ItemTransactionRequestDto requestDto = new ItemTransactionRequestDto();
        requestDto.setAssociateId(40L);
        ItemDto itemDto = new ItemDto();
        Associate associate = new Associate();
        associate.setType(AssociateType.SUPPLIER);

        when(associateDao.findById(accountId, requestDto.getAssociateId())).thenReturn(associate);
        SavedItemValidateInputException exception = assertThrows(SavedItemValidateInputException.class,
            () -> savedItemService.validateInputs(requestDto, itemDto, accountId, TransactionType.OUT));
        assertEquals("Failed to get savedItem during `outcomeItem` {account_id = " + accountId +
            " associateId = " + requestDto.getAssociateId() + "}", exception.getMessage());
    }

    @Test
    void validateInputs_Out_NotExistInAccount() {
        ItemTransactionRequestDto requestDto = new ItemTransactionRequestDto();
        requestDto.setAssociateId(40L);
        ItemDto itemDto = new ItemDto();
        Associate associate = new Associate();
        associate.setType(AssociateType.CLIENT);

        when(associateDao.findById(accountId, requestDto.getAssociateId())).thenReturn(associate);
        when(itemDao.isExistItemById(requestDto.getItemId(), accountId)).thenReturn(false);
        SavedItemValidateInputException exception = assertThrows(SavedItemValidateInputException.class,
            () -> savedItemService.validateInputs(requestDto, itemDto, accountId, TransactionType.OUT));
        assertEquals("Failed to get savedItem during `outcomeItem` {account_id = " + accountId +
            " associateId = " + requestDto.getAssociateId() + "}", exception.getMessage());
    }

    @Test
    void isEnoughCapacityInWarehouse() {
        ItemTransactionRequestDto transactionRequestDto = new ItemTransactionRequestDto();
        transactionRequestDto.setDestinationWarehouseId(37L);
        transactionRequestDto.setQuantity(2L);
        ItemDto itemDto = new ItemDto();
        itemDto.setVolume(5);
        Warehouse warehouse = new Warehouse();
        warehouse.setCapacity(100);

        doReturn(20F).when(savedItemService)
            .toVolumeOfPassSavedItems(transactionRequestDto.getDestinationWarehouseId(), accountId);
        when(warehouseDao.findById(transactionRequestDto.getDestinationWarehouseId(), accountId)).thenReturn(warehouse);
        assertEquals(savedItemService.isEnoughCapacityInWarehouse(transactionRequestDto, itemDto, accountId), true);

    }

    @Test
    void isEnoughCapacityInWarehouse_NotEnoughCapacityInWarehouse() {
        ItemTransactionRequestDto transactionRequestDto = new ItemTransactionRequestDto();
        transactionRequestDto.setDestinationWarehouseId(37L);
        transactionRequestDto.setQuantity(2L);
        ItemDto itemDto = new ItemDto();
        itemDto.setVolume(5);
        Warehouse warehouse = new Warehouse();
        warehouse.setCapacity(0);

        doReturn(20F).when(savedItemService)
            .toVolumeOfPassSavedItems(transactionRequestDto.getDestinationWarehouseId(), accountId);
        when(warehouseDao.findById(transactionRequestDto.getDestinationWarehouseId(), accountId)).thenReturn(warehouse);
        assertEquals(savedItemService.isEnoughCapacityInWarehouse(transactionRequestDto, itemDto, accountId), false);

    }

    @Test
    void toVolumeOfPassSavedItems() {
        List<SavedItem> savedItems = getListOfSavedItems();
        List<Item> items = getListOfItems();

        when(savedItemDao.existSavedItemByWarehouseId(warehouseId)).thenReturn(true);
        when(savedItemDao.findSavedItemByWarehouseId(warehouseId)).thenReturn(savedItems);
        String itemIds = savedItems.stream().map(x -> x.getItemId().toString()).collect(Collectors.joining(","));
        when(itemDao.findItemsById(itemIds, accountId)).thenReturn(items);
        assertTrue(savedItemService.toVolumeOfPassSavedItems(warehouseId, accountId) > 0);
    }

    @Test
    void toVolumeOfPassSavedItems_NotExistSavedItemByWarehouseId() {
        when(savedItemDao.existSavedItemByWarehouseId(warehouseId)).thenReturn(false);
        assertEquals(0, savedItemService.toVolumeOfPassSavedItems(warehouseId, accountId));

    }

    @Test
    void toVolumeOfPassSavedItems_SavedItemsEmpty() {
        List<SavedItem> savedItems = new ArrayList<>();

        when(savedItemDao.existSavedItemByWarehouseId(warehouseId)).thenReturn(true);
        when(savedItemDao.findSavedItemByWarehouseId(warehouseId)).thenReturn(savedItems);
        assertEquals(0, savedItemService.toVolumeOfPassSavedItems(warehouseId, accountId));

    }

    @Test
    void isLowSpaceInWarehouse() {
        Warehouse warehouse = new Warehouse();
        warehouse.setCapacity(40);
        ItemTransactionRequestDto itemTransaction = new ItemTransactionRequestDto();
        itemTransaction.setDestinationWarehouseId(37L);

        doReturn(10F).when(savedItemService).toVolumeOfPassSavedItems(itemTransaction.getDestinationWarehouseId(),
            accountId);
        when(warehouseDao.findById(itemTransaction.getDestinationWarehouseId(), accountId)).thenReturn(warehouse);
        assertEquals(savedItemService.isLowSpaceInWarehouse(itemTransaction, accountId), false);
    }

    @Test
    void isLowSpaceInWarehouse_VolumeZero() {
        ItemTransactionRequestDto itemTransaction = new ItemTransactionRequestDto();
        itemTransaction.setDestinationWarehouseId(37L);

        doReturn(0F).when(savedItemService).toVolumeOfPassSavedItems(itemTransaction.getDestinationWarehouseId(),
            accountId);
        assertEquals(savedItemService.isLowSpaceInWarehouse(itemTransaction, accountId), true);
    }

    @Test
    void isLowSpaceInWarehouse_WarehouseLoadMoreThenMaxWarehouseLoad() {
        Warehouse warehouse = new Warehouse();
        warehouse.setCapacity(2);
        ItemTransactionRequestDto itemTransaction = new ItemTransactionRequestDto();
        itemTransaction.setDestinationWarehouseId(37L);

        doReturn(10F).when(savedItemService).toVolumeOfPassSavedItems(itemTransaction.getDestinationWarehouseId(),
            accountId);
        when(warehouseDao.findById(itemTransaction.getDestinationWarehouseId(), accountId)).thenReturn(warehouse);
        assertEquals(savedItemService.isLowSpaceInWarehouse(itemTransaction, accountId), true);
    }

    @Test
    void existInAccount() {
        ItemTransactionRequestDto itemTransaction = new ItemTransactionRequestDto();
        itemTransaction.setDestinationWarehouseId(37L);
        itemTransaction.setItemId(108L);
        Warehouse warehouse = new Warehouse();
        warehouse.setAccountID(2L);

        when(itemDao.isExistItemById(itemTransaction.getItemId(), accountId)).thenReturn(true);
        when(warehouseDao.findById(itemTransaction.getDestinationWarehouseId(), accountId)).thenReturn(warehouse);
        assertEquals(savedItemService.existInAccount(itemTransaction, accountId), true);
    }

    @Test
    void existInAccount_NotExistItemById() {
        ItemTransactionRequestDto itemTransaction = new ItemTransactionRequestDto();
        itemTransaction.setItemId(108L);

        when(itemDao.isExistItemById(itemTransaction.getItemId(), accountId)).thenReturn(false);
        assertEquals(savedItemService.existInAccount(itemTransaction, accountId), false);
    }

    @Test
    void existInAccount_NotValidWarehouseAccountId() {
        ItemTransactionRequestDto itemTransaction = new ItemTransactionRequestDto();
        itemTransaction.setDestinationWarehouseId(37L);
        itemTransaction.setItemId(108L);
        Warehouse warehouse = new Warehouse();
        warehouse.setAccountID(3L);

        when(itemDao.isExistItemById(itemTransaction.getItemId(), accountId)).thenReturn(true);
        when(warehouseDao.findById(itemTransaction.getDestinationWarehouseId(), accountId)).thenReturn(warehouse);
        assertEquals(savedItemService.existInAccount(itemTransaction, accountId), false);
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

    private List<SavedItem> getListOfSavedItems() {
        List<SavedItem> items = new ArrayList<>();
        SavedItem trout = new SavedItem();
        trout.setId(70L);
        trout.setQuantity(5);
        trout.setItemId(109L);
        trout.setWarehouseId(37L);

        items.add(trout);

        SavedItem salmon = new SavedItem();
        salmon.setId(71L);
        salmon.setQuantity(5);
        salmon.setItemId(110L);
        salmon.setWarehouseId(38L);

        items.add(salmon);

        SavedItem catfish = new SavedItem();
        catfish.setId(72L);
        catfish.setQuantity(5);
        catfish.setItemId(111L);
        catfish.setWarehouseId(39L);

        items.add(catfish);
        return items;
    }

}
