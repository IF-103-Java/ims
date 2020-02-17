package com.ita.if103java.ims.service;

import ch.qos.logback.classic.spi.ILoggingEvent;
import com.ita.if103java.ims.dao.AddressDao;
import com.ita.if103java.ims.dao.SavedItemDao;
import com.ita.if103java.ims.dao.WarehouseDao;
import com.ita.if103java.ims.dto.AddressDto;
import com.ita.if103java.ims.dto.WarehouseDto;
import com.ita.if103java.ims.entity.AccountType;
import com.ita.if103java.ims.entity.Address;
import com.ita.if103java.ims.entity.Event;
import com.ita.if103java.ims.entity.Role;
import com.ita.if103java.ims.entity.SavedItem;
import com.ita.if103java.ims.entity.User;
import com.ita.if103java.ims.entity.Warehouse;
import com.ita.if103java.ims.exception.service.MaxWarehousesLimitReachedException;
import com.ita.if103java.ims.exception.service.WarehouseDeleteException;
import com.ita.if103java.ims.mapper.dto.AddressDtoMapper;
import com.ita.if103java.ims.mapper.dto.WarehouseDtoMapper;
import com.ita.if103java.ims.security.UserDetailsImpl;
import com.ita.if103java.ims.service.impl.WarehouseServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;


@ExtendWith(SpringExtension.class)
public class WarehouseServiceImplTest {
    private WarehouseDto warehouseDto = new WarehouseDto(12L, "WarehouseTest", "auto parts", 20, true, 5L, 1L, 4L, true, null);
    private WarehouseDto topWarehouseDto = new WarehouseDto(1L, "WarehouseTop", "auto parts", 0, false, null, 1L, null, true, null);

    @Mock
    private Warehouse warehouse;
    @Mock
    private WarehouseDao warehouseDao;
    @Mock
    private AddressDao addressDao;
    @Mock
    private AddressDtoMapper addressDtoMapper;
    @Mock
    private EventService eventService;
    @Mock
    private SavedItemDao savedItemDao;
    @Mock
    private WarehouseDtoMapper warehouseDtoMapper;

    @InjectMocks
    private WarehouseServiceImpl warehouseService;

    private UserDetailsImpl userDetails;


    @BeforeEach
    void setUp() {
        MockitoAnnotations.initMocks(this);
        User user = new User();
        user.setAccountId(1L);
        user.setRole(Role.ROLE_WORKER);
        userDetails = new UserDetailsImpl(user);


        List<Warehouse> warehouseList = Arrays.asList(
            new Warehouse(2L, "Store2", "auto parts", 0, false, 2L, 2L,
                2L, true),
            new Warehouse(3L, "Store3!", "tyres", 50, true, 2L, 2L,
                2L, true));
    }

    @Test
    public void addWarehouse_testMaxWarehouseNumberReached() {
        int quantity = 3;
        Long accountId = topWarehouseDto.getAccountID();
        assertNull(topWarehouseDto.getParentID());
        when(warehouseDao.findQuantityOfWarehousesByAccountId(1L)).thenReturn(quantity);
        MaxWarehousesLimitReachedException exception = assertThrows(MaxWarehousesLimitReachedException.class, () -> {
            warehouseService.add(warehouseDto, userDetails);
        });
        assertEquals("The maximum number of warehouses has been reached for this" + "{accountId = " + accountId + "}", exception.getMessage());
       }

    @Test
    void findByIdTest_isWarehouseTopLevel() {
        Warehouse targetWarehouse = new Warehouse();
        WarehouseDto warehouseDto = new WarehouseDto();
        AddressDto addressDto = new AddressDto();
        Address address = new Address();
        when(warehouseDao.findById(1L, 1L)).thenReturn(targetWarehouse);
        when(warehouseDtoMapper.toDto(targetWarehouse)).thenReturn(warehouseDto);
        assertTrue(targetWarehouse.isTopLevel());
        when(addressDao.findByWarehouseId(1L)).thenReturn(address);
        when(addressDtoMapper.toDto(address)).thenReturn(addressDto);

        assertEquals(warehouseService.findById(1L, userDetails), warehouseDto);
    }

    @Test
    void findByIdTest_isNotWarehouseTopLevel() {
        Warehouse targetWarehouse = new Warehouse();
        WarehouseDto warehouseDto = new WarehouseDto();
        when(warehouseDao.findById(1L, 1L)).thenReturn(targetWarehouse);
        when(warehouseDtoMapper.toDto(targetWarehouse)).thenReturn(warehouseDto);
        assertFalse(targetWarehouse.isTopLevel());
        when(warehouseDao.findByTopWarehouseID(2L, 1L)).thenReturn(Collections.emptyList());

        assertEquals(warehouseService.findById(1L, userDetails), warehouseDto);
    }



    @Test
    void findAllTopLevelTest() {


    }

    @Test
    void findWarehousesByTopLevelId_Test() {
        when(warehouseDao.findByTopWarehouseID(1L, 1L)).thenReturn(List.of(warehouse));
    }


    @Test
    public void softDeleteTest_throwExceptionWhenSubWarehouseExists() {
        Warehouse targetWarehouse = new Warehouse();

        when(warehouseDao.findById(1L, 1L)).thenReturn(targetWarehouse);
        when(warehouseDao.findChildrenById(1L, 1L)).thenReturn(List.of(new Warehouse()));

        WarehouseDeleteException warehouseDeleteException = assertThrows(WarehouseDeleteException.class, () -> {
            warehouseService.softDelete(1L, userDetails);
        });
        assertEquals("Warehouse has sub warehouses! Firstly you should delete them!", warehouseDeleteException.getMessage());
    }

    @Test
    public void softDeleteTest_throwExceptionWhenSavedItemExists() {
        Warehouse targetWarehouse = new Warehouse();

        when(warehouseDao.findById(1L, 1L)).thenReturn(targetWarehouse);
        when(warehouseDao.findChildrenById(1L, 1L)).thenReturn(Collections.emptyList());
        when(savedItemDao.findSavedItemByWarehouseId(1L)).thenReturn(List.of(new SavedItem()));

        WarehouseDeleteException warehouseDeleteException = assertThrows(WarehouseDeleteException.class, () -> {
            warehouseService.softDelete(1L, userDetails);
        });
        assertEquals("Warehouse is not empty! Firstly you should remove or transfer all items from that warehouse to another", warehouseDeleteException.getMessage());
    }

    @Test
    public void softDeleteTest_warehouseIsNotDeleted() {
        Warehouse targetWarehouse = new Warehouse();

        when(warehouseDao.findById(1L, 1L)).thenReturn(targetWarehouse);
        when(warehouseDao.findChildrenById(1L, 1L)).thenReturn(Collections.emptyList());
        when(savedItemDao.findSavedItemByWarehouseId(1L)).thenReturn(Collections.emptyList());
        when(warehouseDao.softDelete(1L)).thenReturn(false);

        boolean result = warehouseService.softDelete(1L, userDetails);

        assertFalse(result);

        verify(eventService, never()).create(any(Event.class));
    }

    @Test
    public void softDeleteTest_warehouseIsDeleted() {
        Warehouse targetWarehouse = new Warehouse();

        when(warehouseDao.findById(1L, 1L)).thenReturn(targetWarehouse);
        when(warehouseDao.findChildrenById(1L, 1L)).thenReturn(Collections.emptyList());
        when(savedItemDao.findSavedItemByWarehouseId(1L)).thenReturn(Collections.emptyList());
        when(warehouseDao.softDelete(1L)).thenReturn(true);

        boolean result = warehouseService.softDelete(1L, userDetails);

        assertTrue(result);

        verify(eventService).create(any(Event.class));
    }

    @Test
    public void findTotalCapacityTest() {
        when(warehouseDao.findTotalCapacity(1L, 2L)).thenReturn(anyInt());
    }

}
