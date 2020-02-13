package com.ita.if103java.ims.service;

import com.ita.if103java.ims.dao.AddressDao;
import com.ita.if103java.ims.dao.SavedItemDao;
import com.ita.if103java.ims.dao.WarehouseDao;
import com.ita.if103java.ims.dto.WarehouseDto;
import com.ita.if103java.ims.entity.Warehouse;
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
    @Mock
    private UserDetailsImpl userDetails;

    private WarehouseDtoMapper warehouseDtoMapper = new WarehouseDtoMapper();

    @InjectMocks
    private WarehouseServiceImpl warehouseService;

    private Warehouse warehouse;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void addWarehouseTest() {
        Warehouse warehouse = warehouseDtoMapper.toEntity(warehouseDto);
    }

    @Test
    void findByIdTest() {

    }

    @Test
    void findAllTopLevelTest() {

    }

    @Test
    void findWarehousesByTopLevelIdTest() {

    }

    @Test
    public void softDeleteTest() {
//        Mockito.when(warehouseService.)

    }

}
