package com.ita.if103java.ims.service.impl;

import com.ita.if103java.ims.dao.WarehouseDao;
import com.ita.if103java.ims.dto.WarehouseDto;
import com.ita.if103java.ims.entity.User;
import com.ita.if103java.ims.entity.Warehouse;
import com.ita.if103java.ims.exception.MaxWarehouseDepthLimitReachedException;
import com.ita.if103java.ims.exception.MaxWarehousesLimitReachedException;
import com.ita.if103java.ims.mapper.WarehouseDtoMapper;
import com.ita.if103java.ims.security.UserDetailsImpl;
import com.ita.if103java.ims.service.WarehouseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
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
        UserDetailsImpl userDetails = new UserDetailsImpl(new User());
        Long accountId = userDetails.getUser().getAccountId();
        if (warehouseDto.getParentID() == null) {
            int maxWarehouses = userDetails.getAccountType().getMaxWarehouses();
            int warehouseQuantity = warehouseDao.findQuantityOfWarehousesByAccountId(accountId);
            if (warehouseQuantity < maxWarehouses) {
                return createNewWarehouse(warehouseDto);
            } else {
                throw new MaxWarehousesLimitReachedException("The maximum number of warehouses has been reached for this" +
                    "{accountId = " + accountId + "}");
            }
        }

        Integer maxWarehouseDepth = userDetails.getAccountType().getMaxWarehouseDepth();
        int parentLevel = warehouseDao.findLevelByParentID(warehouseDto.getParentID());
        if (parentLevel + 1 < maxWarehouseDepth) {
            return createNewWarehouse(warehouseDto);
        } else {
            throw new MaxWarehouseDepthLimitReachedException("The maximum depth of warehouse's levels has been reached for this" +
                "{accountId = " + accountId + "}");
        }
    }

    private WarehouseDto createNewWarehouse(WarehouseDto warehouseDto) {
        Warehouse warehouse = warehouseDtoMapper.toEntity(warehouseDto);
        return warehouseDtoMapper.toDto(warehouseDao.create(warehouse));
    }

    @Override
    public List<WarehouseDto> findAll(Pageable pageable) {
        return warehouseDtoMapper.toDtoList(warehouseDao.findAll(pageable));
    }

    @Override
    public WarehouseDto findWarehouseById(Long id) {
        return warehouseDtoMapper.toDto(warehouseDao.findById(id));
    }

    @Override
    public List<WarehouseDto> findWarehousesByTopLevelId(Long topLevelId) {
        return warehouseDtoMapper.toDtoList(warehouseDao.findChildrenByTopWarehouseID(topLevelId));
    }

    @Override
    public WarehouseDto update(WarehouseDto warehouseDto) {
        Warehouse updatedWarehouse = warehouseDtoMapper.toEntity(warehouseDto);
        Warehouse dBWarehouse = warehouseDao.findById(updatedWarehouse.getId());
        updatedWarehouse.setActive(dBWarehouse.isActive());
        return warehouseDtoMapper.toDto(warehouseDao.update(updatedWarehouse));
    }

    @Override
    public boolean softDelete(Long id) {
        return warehouseDao.softDelete(id);
    }
}
