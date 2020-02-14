package com.ita.if103java.ims.service;

import com.ita.if103java.ims.dao.AddressDao;
import com.ita.if103java.ims.dao.SavedItemDao;
import com.ita.if103java.ims.dao.WarehouseDao;
import com.ita.if103java.ims.dto.WarehouseDto;
import com.ita.if103java.ims.entity.Event;
import com.ita.if103java.ims.entity.SavedItem;
import com.ita.if103java.ims.entity.User;
import com.ita.if103java.ims.entity.Warehouse;
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
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;


@ExtendWith(SpringExtension.class)
public class WarehouseServiceImplTest {
    private WarehouseDto warehouseDto = new WarehouseDto(12L, "WarehouseTest", "auto parts", 20, true, 5L, 2L, 4L, true, null);

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

    private WarehouseDtoMapper warehouseDtoMapper = new WarehouseDtoMapper();

    @InjectMocks
    private WarehouseServiceImpl warehouseService;

    private UserDetailsImpl userDetails;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.initMocks(this);
        User user = new User();
        user.setAccountId(1L);
        userDetails = new UserDetailsImpl(user);
    }

    @Test
    public void addWarehouseTest() {
        Warehouse warehouse = warehouseDtoMapper.toEntity(warehouseDto);
    }

    @Test
    void findByIdTest() {
        Warehouse targetWarehouse = new Warehouse();

    }

    @Test
    void findAllTopLevelTest() {

    }

    @Test
    void findWarehousesByTopLevelIdTest() {

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

        assertEquals(false, result);

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

        assertEquals(true, result);

        verify(eventService).create(any(Event.class));
    }

}
