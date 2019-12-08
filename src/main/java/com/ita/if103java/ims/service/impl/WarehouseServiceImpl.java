package com.ita.if103java.ims.service.impl;

import com.ita.if103java.ims.dao.WarehouseDao;
import com.ita.if103java.ims.dto.WarehouseDto;
import com.ita.if103java.ims.entity.Warehouse;
import com.ita.if103java.ims.mapper.WarehouseDtoMapper;
import com.ita.if103java.ims.service.WarehouseService;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

public class WarehouseServiceImpl implements WarehouseService {
    private WarehouseDao warehouseDao;
    private WarehouseDtoMapper warehouseDtoMapper;


    @Autowired
    public WarehouseServiceImpl(WarehouseDao warehouseDao, WarehouseDtoMapper warehouseDtoMapper) {
        this.warehouseDao = warehouseDao;
        this.warehouseDtoMapper = warehouseDtoMapper;
    }

    @Override
    public WarehouseDto add(WarehouseDto warehouseDto) {
        Warehouse warehouse = warehouseDtoMapper.convertWarehouseDtoToWarehouse(warehouseDto);
        return warehouseDtoMapper.convertWarehouseToWarehouseDto(warehouseDao.create(warehouse));
    }

    @Override
    public List<WarehouseDto> findAll() {
        return warehouseDtoMapper.toDtoList(warehouseDao.findAll());
    }

    @Override
    public WarehouseDto findWarehouseById(Long id) {
        return warehouseDtoMapper.convertWarehouseToWarehouseDto(warehouseDao.findById(id));
    }

    @Override
    public List<WarehouseDto> findWarehousesByParentId(Long parentId) {
        return warehouseDtoMapper.toDtoList(warehouseDao.findChildrenByID(parentId));
    }

    @Override
    public WarehouseDto update(WarehouseDto warehouseDto) {
        Warehouse updatedWarehouse = warehouseDtoMapper.convertWarehouseDtoToWarehouse(warehouseDto);
        Warehouse DBWarehouse = warehouseDao.findById(updatedWarehouse.getId());
        updatedWarehouse.setActive(DBWarehouse.isActive());
        return warehouseDtoMapper.convertWarehouseToWarehouseDto(warehouseDao.update(updatedWarehouse));
    }

    @Override
    public boolean softDelete(Long id) {return warehouseDao.softDelete(id);
    }
}
