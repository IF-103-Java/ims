package com.ita.if103java.ims.service.impl;

import com.ita.if103java.ims.dao.AddressDao;
import com.ita.if103java.ims.dao.WarehouseDao;
import com.ita.if103java.ims.dto.WarehouseDto;
import com.ita.if103java.ims.entity.Address;
import com.ita.if103java.ims.entity.Event;
import com.ita.if103java.ims.entity.EventName;
import com.ita.if103java.ims.entity.Warehouse;
import com.ita.if103java.ims.exception.MaxWarehouseDepthLimitReachedException;
import com.ita.if103java.ims.exception.MaxWarehousesLimitReachedException;
import com.ita.if103java.ims.mapper.AddressDtoMapper;
import com.ita.if103java.ims.mapper.WarehouseDtoMapper;
import com.ita.if103java.ims.security.UserDetailsImpl;
import com.ita.if103java.ims.service.EventService;
import com.ita.if103java.ims.service.WarehouseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

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
        Address address = addressDtoMapper.toEntity(warehouseDto.getWarehouseAddressDto());
        addressDao.createWarehouseAddress(warehouse.getId(), address);
        createEvent(user, warehouse, EventName.WAREHOUSE_CREATED);

        Warehouse newWarehouse = warehouseDao.create(warehouse);
        populatePath(newWarehouse, user);
        return warehouseDtoMapper.toDto(newWarehouse);
    }

    @Override
    public List<WarehouseDto> findAll(Pageable pageable, UserDetailsImpl user) {
        List<Warehouse> all = warehouseDao.findAll(pageable, user.getUser().getAccountId());
        Map<Long, Warehouse> groupedWarehouses = all.stream()
            .collect(Collectors.toMap(Warehouse::getId, Function.identity()));
        all.forEach(o -> findPath(o, groupedWarehouses));
        return warehouseDtoMapper.toDtoList(all);
    }

    @Override
    public WarehouseDto findById(Long id) {
        Warehouse warehouse = warehouseDao.findById(id);
        populatePath(warehouse, null);
        return warehouseDtoMapper.toDto(warehouse);
    }

    private void populatePath(Warehouse warehouse, UserDetailsImpl userDetails) {
        Map<Long, Warehouse> groupedWarehouses = new HashMap<>();
        if (!warehouse.getId().equals(warehouse.getTopWarehouseID())) {
            List<Warehouse> warehousesInHieararchy = warehouseDao.findByTopWarehouseID(warehouse.getTopWarehouseID());
            groupedWarehouses = warehousesInHieararchy.stream()
                .collect(Collectors.toMap(Warehouse::getId, Function.identity()));
        } else {
            groupedWarehouses.put(warehouse.getId(), warehouse);
        }
        findPath(warehouse, groupedWarehouses);
    }

    @Override
    public List<WarehouseDto> findWarehousesByTopLevelId(Long topLevelId, UserDetailsImpl user) {
        List<Warehouse> byTopWarehouseID = warehouseDao.findByTopWarehouseID(topLevelId);
        Map<Long, Warehouse> groupedWarehouses = byTopWarehouseID.stream()
            .collect(Collectors.toMap(Warehouse::getId, Function.identity()));
        byTopWarehouseID.forEach(o -> findPath(o, groupedWarehouses));
        return warehouseDtoMapper.toDtoList(byTopWarehouseID);
    }

    @Override
    public WarehouseDto update(WarehouseDto warehouseDto, UserDetailsImpl user) {
        Warehouse updatedWarehouse = warehouseDtoMapper.toEntity(warehouseDto);
        Warehouse dBWarehouse = warehouseDao.findById(updatedWarehouse.getId());
        updatedWarehouse.setActive(dBWarehouse.isActive());
        createEvent(user, updatedWarehouse, EventName.WAREHOUSE_EDITED);
        Warehouse editedWarehouse = warehouseDao.update(updatedWarehouse);
        populatePath(editedWarehouse, user);
        return warehouseDtoMapper.toDto(editedWarehouse);
    }

    @Override
    public boolean softDelete(Long id, UserDetailsImpl user) {
        Boolean isDelete = warehouseDao.softDelete(id);
        if (isDelete) {
            Warehouse warehouse = warehouseDao.findById(id);
            createEvent(user, warehouse, EventName.WAREHOUSE_REMOVED);
        }
        return isDelete;
    }

    private void createEvent(UserDetailsImpl user, Warehouse warehouse, EventName eventName) {
        Event event = new Event();
        String message = eventName.getLabel();
        int level = warehouseDao.findLevelByParentID(warehouse.getParentID());
        if (eventName == EventName.WAREHOUSE_CREATED) {
            message += " Name : " + warehouse.getName() + "level : " + level;
        }
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
            List<String> parentPath = parentWarehouse.getPath();
            if (!parentWarehouse.getPath().isEmpty()) {

                parentPath = parentWarehouse.getPath();
                System.out.println("Parent path exists: " + parentPath);
            } else {
                parentPath = findPath(parentWarehouse, groupedWarehouses);
                System.out.println("Parent path computed: " + parentPath);
            }
            path.addAll(parentPath);
        }
        List<String> reversePath = new ArrayList<>(path);
        Collections.reverse(reversePath);
        warehouse.setPath(reversePath);
        return path;
    }
}
