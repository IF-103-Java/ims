package com.ita.if103java.ims.mapper;

import com.ita.if103java.ims.dto.AddressDto;
import com.ita.if103java.ims.dto.WarehouseDto;
import com.ita.if103java.ims.entity.Warehouse;
import com.ita.if103java.ims.mapper.dto.WarehouseDtoMapper;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertEquals;

@ExtendWith(MockitoExtension.class)
public class WarehouseDtoMapperTest {
    private Warehouse warehouse = new Warehouse(5L, "WarehouseM", "info", 30, true,
        3L, 2L, 4L, true);

    private WarehouseDto warehouseDto = new WarehouseDto(warehouse.getId(), warehouse.getName(),warehouse.getInfo(),
        warehouse.getCapacity(),warehouse.isBottom(), warehouse.getParentID(), warehouse.getAccountID(),
        warehouse.getTopWarehouseID(), warehouse.isActive(), null);

    private AddressDto addressDto = new AddressDto(10L, "Ukraine", "Kyiv", "Stusa 23, 5,",
        "78000", 48.58F, 52.3F);

    private WarehouseDtoMapper warehouseDtoMapper = new WarehouseDtoMapper();

    @Test
    void testToDto() {
        assertEquals(warehouseDto, warehouseDtoMapper.toDto(warehouse));
    }

    @Test
    void testToEntity() {
        assertEquals(warehouse, warehouseDtoMapper.toEntity(warehouseDto));
    }
}
