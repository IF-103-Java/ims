package com.ita.if103java.ims.service;

import com.ita.if103java.ims.dao.AddressDao;
import com.ita.if103java.ims.dao.SavedItemDao;
import com.ita.if103java.ims.dao.WarehouseDao;
import com.ita.if103java.ims.dto.AddressDto;
import com.ita.if103java.ims.dto.UsefulWarehouseDto;
import com.ita.if103java.ims.dto.WarehouseDto;
import com.ita.if103java.ims.entity.AccountType;
import com.ita.if103java.ims.entity.Address;
import com.ita.if103java.ims.entity.Event;
import com.ita.if103java.ims.entity.EventName;
import com.ita.if103java.ims.entity.Role;
import com.ita.if103java.ims.entity.SavedItem;
import com.ita.if103java.ims.entity.User;
import com.ita.if103java.ims.entity.Warehouse;
import com.ita.if103java.ims.exception.service.MaxWarehouseDepthLimitReachedException;
import com.ita.if103java.ims.exception.service.WarehouseDeleteException;
import com.ita.if103java.ims.exception.service.WarehouseUpdateException;
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
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertLinesMatch;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;


@ExtendWith(SpringExtension.class)
public class WarehouseServiceImplTest {
    private WarehouseDto warehouseDto;
    private WarehouseDto topWarehouseDto;
    private AccountType basic;
    private UserDetailsImpl userDetails;
    private Long accountId;
    private Address address;
    private AddressDto addressDto;

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
    @Mock
    private SavedItemService savedItemService;

    @InjectMocks
    private WarehouseServiceImpl warehouseService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.initMocks(this);
        basic = new AccountType();
        basic.setMaxWarehouses(3);
        basic.setMaxWarehouseDepth(3);
        basic.setId(1L);
        User user = new User();
        user.setId(1L);
        user.setAccountId(1L);
        user.setRole(Role.ROLE_WORKER);
        userDetails = new UserDetailsImpl(user);
        userDetails.setAccountType(basic);
        topWarehouseDto = new WarehouseDto(1L, "WarehouseTop", "auto parts", 0, false, null, 1L, null, true, null);
        warehouseDto = new WarehouseDto(12L, "WarehouseTest", "auto parts", 20, false, 1L, 1L, 1L, true, null);
        address = new Address("Ukraine", "Kyiv", "Stusa, 5", "77000", 48F, 50F);
        addressDto = new AddressDto(1L, "Ukraine", "Kyiv", "Stusa, 5", "77000", 48F, 50F);
    }

    @Test
    public void add_successFlow() {
        int quantity = 2;
        int maxQuantity = userDetails.getAccountType().getMaxWarehouses();
        WarehouseDto result = new WarehouseDto(1L, "WarehouseTop", "auto parts", 0, false, null, 1L, null, true, addressDto);
        Warehouse warehouseCreate = new Warehouse(1L, "WarehouseTop", "auto parts", 0, false, null, 1L, null, true);
        warehouseDto.setParentID(null);
        Long accountId = topWarehouseDto.getAccountID();
        assertNull(topWarehouseDto.getParentID());
        when(warehouseDao.findQuantityOfWarehousesByAccountId(accountId)).thenReturn(quantity);
        assertTrue(quantity < maxQuantity);
        when(warehouseDao.create(warehouseDtoMapper.toEntity(warehouseDto))).thenReturn(warehouseCreate);
        when(addressDtoMapper.toEntity(warehouseDto.getAddressDto())).thenReturn(address);
        assertTrue(warehouseCreate.isTopLevel());
        when(addressDao.createWarehouseAddress(warehouseCreate.getId(), address)).thenReturn(address);
        when(addressDtoMapper.toDto(address)).thenReturn(addressDto);
        Event event = new Event("Warehouse created " +
            topWarehouseDto.getName(), accountId,
            topWarehouseDto.getId(), 1L, EventName.WAREHOUSE_CREATED, 2L);
        doNothing().when(eventService).create(event);
        when(warehouseDtoMapper.toDto(warehouseCreate)).thenReturn(topWarehouseDto);
        topWarehouseDto.setAddressDto(addressDto);
        assertEquals(warehouseService.add(result, userDetails), topWarehouseDto);
    }

    @Test
    public void addWarehouse_testMaxDepthReached() {
        Warehouse level1 = new Warehouse(1L, "Level1", "auto parts", null, false, null, 1L, 1L, true);
        Warehouse level2 = new Warehouse(2L, "level2", "auto parts", null, false, 1L, 1L, 1L, true);
        Warehouse level3 = new Warehouse(3L, "level3", "auto parts", null, false, 2L, 1L, 1L, true);
        WarehouseDto level4DTO = new WarehouseDto(4L, "level4", "auto parts", 30, true, 3L, 1L, 1L, true, null);

        int level = 4;
        accountId = userDetails.getUser().getAccountId();
        int maxDepth = userDetails.getAccountType().getMaxWarehouseDepth();
        assertNotNull(warehouseDto.getParentID());
        when(warehouseDao.findById(anyLong(), eq(accountId))).thenReturn(level3);
        when(warehouseDao.findLevelByParentID(level4DTO.getParentID())).thenReturn(3);
        assertFalse(level < maxDepth);
        MaxWarehouseDepthLimitReachedException exception = assertThrows(MaxWarehouseDepthLimitReachedException.class, () -> {
            warehouseService.add(level4DTO, userDetails);
        });
        assertEquals("The maximum depth of warehouse's levels has been reached for this" +
            "{accountId = " + accountId + "}", exception.getMessage());
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
    void findAllTopLevel_pageble_success() {
        Long accountId = userDetails.getUser().getAccountId();
        List<Warehouse> warehouseList = getListOfWarehouses();
        List<WarehouseDto> warehouseDtoList = getListOfWarehouseDtos();
        Integer warehouseQuantity = 3;
        PageRequest pageable = PageRequest.of(0, 3, Sort.Direction.ASC, "id");
        Page page = new PageImpl(warehouseDtoList, pageable, warehouseQuantity);
        when(warehouseDao.findQuantityOfWarehousesByAccountId(accountId)).thenReturn(warehouseQuantity);
        when(warehouseDao.findAllTopLevel(pageable, accountId)).thenReturn(warehouseList);
        when(warehouseDtoMapper.toDto(warehouseList.get(0))).thenReturn(warehouseDtoList.get(0));
        when(warehouseDtoMapper.toDto(warehouseList.get(1))).thenReturn(warehouseDtoList.get(1));
        when(warehouseDtoMapper.toDto(warehouseList.get(2))).thenReturn(warehouseDtoList.get(2));
        when(addressDao.findByWarehouseId(warehouse.getId())).thenReturn(address);
        when(addressDtoMapper.toDto(address)).thenReturn(addressDto);
        warehouseDtoList.get(0).setAddressDto(addressDto);
        warehouseDtoList.get(1).setAddressDto(addressDto);
        warehouseDtoList.get(2).setAddressDto(addressDto);
        assertEquals(warehouseService.findAllTopLevel(pageable, userDetails), page);
    }


    @Test
    void update_notActive() {
        Warehouse inActive = new Warehouse(12L, "WarehouseTest", "auto parts", 20, true, 5L, 1L, 4L, false);
        when(warehouseDtoMapper.toEntity(warehouseDto)).thenReturn(inActive);
        assertFalse(warehouse.isActive());
        WarehouseUpdateException exception = assertThrows(WarehouseUpdateException.class, () -> {
            warehouseService.update(warehouseDto, userDetails);
        });
        assertEquals("You can't make warehouse inactive!", exception.getMessage());
    }

    @Test
    void findWarehousesByTopLevelId_Test() {
        List<Warehouse> warehouseList = Arrays.asList(
            new Warehouse(4L, "Store2", "auto parts", 0, false, 2L, 2L,
                1L, true),
            new Warehouse(3L, "Store3!", "tyres", 50, true, 2L, 2L,
                1L, true));
        when(warehouseDao.findByTopWarehouseID(1L, 1L)).thenReturn(List.of(warehouse));
        when(warehouseDtoMapper.toDtoList(warehouseList)).thenReturn(List.of(warehouseDto));
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

    private List<WarehouseDto> getListOfWarehouseDtos() {
        Long accountId = userDetails.getUser().getAccountId();
        List<WarehouseDto> warehouseDtos = new ArrayList<>();
        WarehouseDto stockA = new WarehouseDto();
        stockA.setName("stockA");
        stockA.setInfo("products");
        stockA.setCapacity(null);
        stockA.setParentID(null);
        stockA.setAccountID(accountId);
        stockA.setTopWarehouseID(null);
        stockA.setActive(true);
        stockA.setAddressDto(null);
        stockA.setId(9L);

        warehouseDtos.add(stockA);

        WarehouseDto stockB = new WarehouseDto();
        stockB.setName("stockB");
        stockB.setInfo("products");
        stockB.setCapacity(null);
        stockB.setParentID(null);
        stockB.setAccountID(accountId);
        stockB.setTopWarehouseID(null);
        stockB.setActive(true);
        stockB.setAddressDto(null);
        stockB.setId(10L);

        warehouseDtos.add(stockB);

        WarehouseDto stockC = new WarehouseDto();
        stockC.setName("stockC");
        stockC.setInfo("products");
        stockC.setCapacity(null);
        stockC.setParentID(null);
        stockC.setAccountID(accountId);
        stockC.setTopWarehouseID(null);
        stockC.setActive(true);
        stockC.setAddressDto(null);
        stockC.setId(11L);

        warehouseDtos.add(stockC);
        return warehouseDtos;
    }

    @Test
    void findUsefulWarehouses() {
        Long capacity = 8L;
        User user = new User();
        user.setAccountId(2L);
        user.setId(1L);
        UserDetailsImpl userDetails = new UserDetailsImpl(user);
        Long accountId = user.getAccountId();
        List<Warehouse> usefulWarehouse = new ArrayList<>();
        Warehouse sectionA = new Warehouse(3L, "SectionA", "some info", 100, true, 1L, 2L, 1L, true);
        usefulWarehouse.add(sectionA);
        Warehouse sectionB = new Warehouse(4L, "SectionB", "some info", 100, true, 2L, 2L, 2L, true);
        usefulWarehouse.add(sectionB);
        List<Warehouse> usefulTopWarehouse = new ArrayList<>();
        Warehouse warehouseA = new Warehouse(1L, "WarehouseA", "some info", 0, false, null, 2L, 1L, true);
        Warehouse warehouseB = new Warehouse(2L, "WarehouseB", "some info", 0, false, null, 2L, 2L, true);
        usefulTopWarehouse.add(warehouseA);
        usefulTopWarehouse.add(warehouseB);
        usefulTopWarehouse.add(sectionA);
        usefulTopWarehouse.add(sectionB);
        when(warehouseDao.findUsefulTopWarehouse(capacity, accountId)).thenReturn(usefulWarehouse);
        String ids = usefulWarehouse.stream().
            map(x -> x.getTopWarehouseID().toString()).collect(Collectors.joining(","));
        when(warehouseDao.findByTopWarehouseIDs(ids, accountId)).thenReturn(usefulTopWarehouse);
        List<UsefulWarehouseDto> usefulWarehouseDtos = warehouseService.findUsefulWarehouses(capacity, userDetails);
        when(savedItemService.toVolumeOfPassSavedItems(anyLong(), anyLong())).thenReturn(5F);
        assertLinesMatch(usefulWarehouseDtos.get(0).getPath(), List.of("WarehouseA", "SectionA"));
        assertLinesMatch(usefulWarehouseDtos.get(1).getPath(), List.of("WarehouseB", "SectionB"));
    }

    private List<Warehouse> getListOfWarehouses() {
        Long accountId = userDetails.getUser().getAccountId();
        List<Warehouse> warehouses = new ArrayList<>();
        Warehouse stockA = new Warehouse();
        stockA.setName("stockA");
        stockA.setInfo("products");
        stockA.setCapacity(null);
        stockA.setParentID(null);
        stockA.setAccountID(accountId);
        stockA.setTopWarehouseID(null);
        stockA.setActive(true);
        stockA.setId(12L);

        Warehouse stockB = new Warehouse();
        stockB.setName("stockB");
        stockB.setInfo("products");
        stockB.setCapacity(null);
        stockB.setParentID(null);
        stockB.setAccountID(accountId);
        stockB.setTopWarehouseID(null);
        stockB.setActive(true);
        stockB.setId(13L);

        Warehouse stockC = new Warehouse();
        stockC.setName("stockC");
        stockC.setInfo("products");
        stockC.setCapacity(null);
        stockC.setParentID(null);
        stockC.setAccountID(accountId);
        stockC.setTopWarehouseID(null);
        stockC.setActive(true);
        stockC.setId(14L);

        warehouses.add(stockA);
        warehouses.add(stockB);
        warehouses.add(stockC);
        return warehouses;
    }
}
