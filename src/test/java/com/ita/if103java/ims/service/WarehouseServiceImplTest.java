package com.ita.if103java.ims.service;

import com.ita.if103java.ims.dao.AddressDao;
import com.ita.if103java.ims.dao.SavedItemDao;
import com.ita.if103java.ims.dao.WarehouseDao;
import com.ita.if103java.ims.dto.WarehouseDto;
import com.ita.if103java.ims.entity.Warehouse;
import com.ita.if103java.ims.mapper.dto.AddressDtoMapper;
import com.ita.if103java.ims.mapper.dto.WarehouseDtoMapper;
import com.ita.if103java.ims.service.impl.WarehouseServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(MockitoExtension.class)
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

    private Warehouse warehouse;

    @BeforeEach
    void init() {
        MockitoAnnotations.initMocks(this);
        warehouseService = new WarehouseServiceImpl(warehouseDao, warehouseDtoMapper, addressDao,
            addressDtoMapper, eventService, savedItemDao);

        warehouse = warehouseDtoMapper.toEntity(warehouseDto);
    }

    @Test
    void addTest() {

    }

    @Test
    void findByIdTest() {

    }

    @Test
    void findAllTopLevelTest(){

    }

    @Test
    void findWarehousesByTopLevelIdTest(){

    }

}
