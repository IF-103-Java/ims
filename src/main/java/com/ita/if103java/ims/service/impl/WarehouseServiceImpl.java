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
        EventName eventName = EventName.WAREHOUSE_CREATED;
        createEvent(user, warehouse, eventName);

        return warehouseDtoMapper.toDto(warehouseDao.create(warehouse));
    }

    //without addresses and events
//    private WarehouseDto createNewWarehouse(WarehouseDto warehouseDto) {
//        Warehouse warehouse = warehouseDtoMapper.toEntity(warehouseDto);
//        return warehouseDtoMapper.toDto(warehouseDao.create(warehouse));
//    }

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
        EventName eventName = EventName.WAREHOUSE_EDITED;
        createEvent(user, updatedWarehouse, eventName);
        return warehouseDtoMapper.toDto(warehouseDao.update(updatedWarehouse));
    }

    @Override
    public boolean softDelete(Long id, UserDetailsImpl user) {
        Boolean isDelete = warehouseDao.softDelete(id) ;
        if (isDelete){
        EventName eventName = EventName.WAREHOUSE_REMOVED;
        Warehouse warehouse = warehouseDao.findById(id);
        createEvent(user, warehouse, eventName);
        }
        return isDelete;
    }

    private void createEvent(UserDetailsImpl user, Warehouse warehouse, EventName eventName) {
        Event event = new Event();
        event.setMessage(eventName.getLabel() + " Name : " +
                         warehouse.getName() + "id : " + warehouse.getId());
        event.setAccountId(user.getUser().getAccountId());
        event.setAuthorId(user.getUser().getId());
        event.setName(eventName);

        eventService.create(event);
    }
}
