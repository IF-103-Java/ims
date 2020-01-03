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
import java.util.List;

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
                                EventService eventService
    ) {

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
            return createNewWarehouse(warehouseDto,userDetails);
        } else {
            throw new MaxWarehouseDepthLimitReachedException("The maximum depth of warehouse's levels has been reached for this" +
                "{accountId = " + accountId + "}");
        }
    }

    private WarehouseDto createNewWarehouse(WarehouseDto warehouseDto, UserDetailsImpl user) {
        Warehouse warehouse = warehouseDao.create(warehouseDtoMapper.toEntity(warehouseDto));
        Address address = addressDtoMapper.toEntity(warehouseDto.getAddressDto());
        addressDao.createWarehouseAddress(warehouse.getId(),address);
        createEvent(user, warehouse, EventName.WAREHOUSE_CREATED);

        return warehouseDtoMapper.toDto(warehouseDao.create(warehouse));
    }

    @Override
    public List<WarehouseDto> findAll(Pageable pageable, UserDetailsImpl user) {
        return warehouseDtoMapper.toDtoList(warehouseDao.findAll(pageable));
    }

    @Override
    public WarehouseDto findWarehouseById(Long id) {
        return warehouseDtoMapper.toDto(warehouseDao.findById(id));
    }

    @Override
    public List<WarehouseDto> findWarehousesByTopLevelId(Long topLevelId, UserDetailsImpl user) {
        return warehouseDtoMapper.toDtoList(warehouseDao.findChildrenByTopWarehouseID(topLevelId));
    }

    @Override
    public WarehouseDto update(WarehouseDto warehouseDto, UserDetailsImpl user) {
        Warehouse updatedWarehouse = warehouseDtoMapper.toEntity(warehouseDto);
        Warehouse dBWarehouse = warehouseDao.findById(updatedWarehouse.getId());
        updatedWarehouse.setActive(dBWarehouse.isActive());
        createEvent(user, updatedWarehouse, EventName.WAREHOUSE_EDITED);
        return warehouseDtoMapper.toDto(warehouseDao.update(updatedWarehouse));
    }

    @Override
    public boolean softDelete(Long id, UserDetailsImpl user) {
        Boolean isDelete = warehouseDao.softDelete(id) ;
        if (isDelete){
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
            message += "as a child of warehouse id " + warehouse.getParentID();
        }
        event.setMessage(message);
        event.setAccountId(user.getUser().getAccountId());
        event.setAuthorId(user.getUser().getId());
        event.setName(eventName);

        eventService.create(event);
    }

    public List<String> findPath(Long id, UserDetailsImpl user){
        List<String> path = new ArrayList<>();
        Warehouse warehouse = warehouseDao.findById(id);
            path.add(warehouse.getName());
//        if ((Long)warehouse.getParentID() != null) {
//
//            Warehouse parentWarehouse = warehouseDao.findById(warehouse.getParentID());
//            List<String> parentPath = new ArrayList<>();
//            if (!parentWarehouse.getPath().isEmpty()) {
//
//                parentPath = parentWarehouse.getPath();
//                System.out.println("Parent path exists: " + parentPath);
//            } else {
//                parentPath = findPath(parentWarehouse, groupedWarehouses);
//                System.out.println("Parent path computed: " + parentPath);
//            }
//            path.addAll(parentPath);
//        }
//        List<String> reversePath = new ArrayList<>(path);
//        Collections.reverse(reversePath);
//        warehouse.setPath(reversePath);
        return path;
    }
}
