package com.ita.if103java.ims.service.impl;

import com.ita.if103java.ims.dao.AddressDao;
import com.ita.if103java.ims.dao.WarehouseDao;
import com.ita.if103java.ims.dto.AddressDto;
import com.ita.if103java.ims.dto.WarehouseDto;
import com.ita.if103java.ims.entity.Address;
import com.ita.if103java.ims.entity.Event;
import com.ita.if103java.ims.entity.EventName;
import com.ita.if103java.ims.entity.Warehouse;
import com.ita.if103java.ims.exception.service.MaxWarehouseDepthLimitReachedException;
import com.ita.if103java.ims.exception.service.MaxWarehousesLimitReachedException;
import com.ita.if103java.ims.mapper.dto.AddressDtoMapper;
import com.ita.if103java.ims.mapper.dto.WarehouseDtoMapper;
import com.ita.if103java.ims.security.UserDetailsImpl;
import com.ita.if103java.ims.service.EventService;
import com.ita.if103java.ims.service.WarehouseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
public class WarehouseServiceImpl implements WarehouseService {
    private WarehouseDao warehouseDao;
    private WarehouseDtoMapper warehouseDtoMapper;
    private AddressDao addressDao;
    private AddressDtoMapper addressDtoMapper;
    private EventService eventService;

    @Autowired
    public WarehouseServiceImpl(WarehouseDao warehouseDao,
                                WarehouseDtoMapper warehouseDtoMapper,
                                AddressDao addressDao,
                                AddressDtoMapper addressDtoMapper,
                                EventService eventService) {
        this.warehouseDao = warehouseDao;
        this.warehouseDtoMapper = warehouseDtoMapper;
        this.addressDao = addressDao;
        this.addressDtoMapper = addressDtoMapper;
        this.eventService = eventService;
    }

    @Override
    @Transactional
    public WarehouseDto add(WarehouseDto warehouseDto, UserDetailsImpl userDetails) {
        Long accountId = userDetails.getUser().getAccountId();
        if (warehouseDto.getParentID() == null) {
            int maxWarehouses = userDetails.getAccountType().getMaxWarehouses();
            int warehouseQuantity = warehouseDao.findQuantityOfWarehousesByAccountId(accountId);
            if (warehouseQuantity < maxWarehouses) {
                return createNewWarehouse(warehouseDto, userDetails);
            } else {
                throw new MaxWarehousesLimitReachedException("The maximum number of warehouses has been reached for this" +
                    "{accountId = " + accountId + "}");
            }
        }

        Integer maxWarehouseDepth = userDetails.getAccountType().getMaxWarehouseDepth();
        int parentLevel = warehouseDao.findLevelByParentID(warehouseDto.getParentID());
        if (parentLevel + 1 < maxWarehouseDepth) {
            return createNewWarehouse(warehouseDto, userDetails);
        } else {
            throw new MaxWarehouseDepthLimitReachedException("The maximum depth of warehouse's levels has been reached for this" +
                "{accountId = " + accountId + "}");
        }
    }


    private WarehouseDto createNewWarehouse(WarehouseDto warehouseDto, UserDetailsImpl user) {
        Warehouse warehouse = warehouseDao.create(warehouseDtoMapper.toEntity(warehouseDto));
        Address address = addressDtoMapper.toEntity(warehouseDto.getAddressDto());
        AddressDto addressDto = null;
        if (warehouse.isTopLevel()) {
            Address warehouseAddress = addressDao.createWarehouseAddress(warehouse.getId(), address);
            addressDto = addressDtoMapper.toDto(warehouseAddress);
        }
        createEvent(user, warehouse, EventName.WAREHOUSE_CREATED);

        populatePath(warehouse, user);
        WarehouseDto createdWarehouseDto = warehouseDtoMapper.toDto(warehouse);
        createdWarehouseDto.setAddressDto(addressDto);
        return createdWarehouseDto;
    }

    @Override
    public Page<WarehouseDto> findAll(Pageable pageable, UserDetailsImpl user) {
        Long accountId = user.getUser().getAccountId();
        Integer warehouseQuantity = warehouseDao.findQuantityOfWarehousesByAccountId(accountId);
        List<Warehouse> all = warehouseDao.findAll(pageable, accountId);
        Map<Long, Warehouse> groupedWarehouses = getGroupedWarehouses(pageable, user, all);

        all.forEach(o -> findPath(o, groupedWarehouses));
        for (Warehouse warehouse : all) {
            WarehouseDto warehouseDto = warehouseDtoMapper.toDto(warehouse);
            if (warehouse.isTopLevel()) {
                AddressDto addressDto = addressDtoMapper.toDto(addressDao.findByWarehouseId(warehouse.getId()));
                warehouseDto.setAddressDto(addressDto);
            }
        }
        List<WarehouseDto> warehouses = warehouseDtoMapper.toDtoList(all);
        return new PageImpl<>(warehouses, pageable, warehouseQuantity);

    }

    private Map<Long, Warehouse> getGroupedWarehouses(Pageable pageable, UserDetailsImpl user,
                                                      List<Warehouse> all) {
        Map<Long, Warehouse> groupedWarehouses = all.stream()
            .collect(Collectors.toMap(Warehouse::getId, Function.identity()));
        if (pageable.isPaged()) {
            List<Warehouse> allUnpaged = warehouseDao.findAll(PageRequest.of(0, Integer.MAX_VALUE),
                user.getUser().getAccountId());
            groupedWarehouses = allUnpaged.stream()
                .collect(Collectors.toMap(Warehouse::getId, Function.identity()));
        }
        return groupedWarehouses;
    }

    @Override
    public WarehouseDto findById(Long id, UserDetailsImpl user) {
        Warehouse warehouse = warehouseDao.findById(id, user.getUser().getAccountId());
        WarehouseDto warehouseDto = warehouseDtoMapper.toDto(warehouse);
        if (warehouse.isTopLevel()) {
            AddressDto addressDto = addressDtoMapper.toDto(addressDao.findByWarehouseId(id));
            warehouseDto.setAddressDto(addressDto);
        }
        populatePath(warehouse, user);
        return warehouseDto;
    }

    private void populatePath(Warehouse warehouse, UserDetailsImpl user) {
        Map<Long, Warehouse> groupedWarehouses = new HashMap<>();
        if (!warehouse.isTopLevel()) {
            List<Warehouse> warehousesInHierarchy = warehouseDao.findByTopWarehouseID(warehouse.getTopWarehouseID(),
                user.getUser().getAccountId());
            groupedWarehouses = warehousesInHierarchy.stream()
                .collect(Collectors.toMap(Warehouse::getId, Function.identity()));
        } else {
            groupedWarehouses.put(warehouse.getId(), warehouse);
        }
        findPath(warehouse, groupedWarehouses);
    }

    @Override
    public List<WarehouseDto> findWarehousesByTopLevelId(Long topLevelId, UserDetailsImpl user) {
        List<Warehouse> byTopWarehouseID = warehouseDao.findByTopWarehouseID(topLevelId, user.getUser().getAccountId());
        Map<Long, Warehouse> groupedWarehouses = byTopWarehouseID.stream()
            .collect(Collectors.toMap(Warehouse::getId, Function.identity()));
        byTopWarehouseID.forEach(o -> findPath(o, groupedWarehouses));
        return warehouseDtoMapper.toDtoList(byTopWarehouseID);
    }

    @Override
    public WarehouseDto update(WarehouseDto warehouseDto, UserDetailsImpl user) {
        Warehouse updatedWarehouse = warehouseDtoMapper.toEntity(warehouseDto);
        Warehouse dBWarehouse = warehouseDao.findById(updatedWarehouse.getId(), user.getUser().getAccountId());
        updatedWarehouse.setActive(dBWarehouse.isActive());
        Address address = addressDtoMapper.toEntity(warehouseDto.getAddressDto());
        if (dBWarehouse.isTopLevel()) {
            addressDao.updateWarehouseAddress(updatedWarehouse.getId(), address);
        }
        createEvent(user, updatedWarehouse, EventName.WAREHOUSE_EDITED);
        Warehouse editedWarehouse = warehouseDao.update(updatedWarehouse);
        populatePath(editedWarehouse, user);
        return warehouseDtoMapper.toDto(editedWarehouse);
    }

    @Override
    public boolean softDelete(Long id, UserDetailsImpl user) {
        boolean isDelete = warehouseDao.softDelete(id);
        if (isDelete) {
            Warehouse warehouse = warehouseDao.findById(id, user.getUser().getAccountId());
            createEvent(user, warehouse, EventName.WAREHOUSE_REMOVED);
        }
        return isDelete;
    }

    private void createEvent(UserDetailsImpl user, Warehouse warehouse, EventName eventName) {
        Event event = new Event();
        int level = 0;
        String message = eventName.getLabel();

        if (warehouse.getParentID() != null) {
            level = warehouseDao.findLevelByParentID(warehouse.getParentID());
        }
        message += " Name : " + warehouse.getName() + " level : " + level;

        if (level != 0) {
            message += " as a child of warehouse id " + warehouse.getParentID();
        }
        event.setMessage(message);
        event.setAccountId(user.getUser().getAccountId());
        event.setAuthorId(user.getUser().getId());
        event.setName(eventName);

        eventService.create(event);
    }

    private List<String> findPath(Warehouse warehouse, Map<Long, Warehouse> groupedWarehouses) {
        List<String> path = warehouse.getPath();
        path.add(warehouse.getName());
        if (warehouse.getParentID() != null) {

            Warehouse parentWarehouse = groupedWarehouses.get(warehouse.getParentID());
            List<String> parentPath;
            if (!parentWarehouse.getPath().isEmpty()) {

                parentPath = parentWarehouse.getPath();
            } else {
                parentPath = findPath(parentWarehouse, groupedWarehouses);
            }
            path.addAll(parentPath);
        }
        List<String> reversePath = new ArrayList<>(path);
        Collections.reverse(reversePath);
        warehouse.setPath(reversePath);
        return path;
    }

    @Override
    public Map<Long, String> findAllWarehouseNames(UserDetailsImpl user) {
        return warehouseDao.findAllWarehouseNames(user.getUser().getAccountId());
    }
}
