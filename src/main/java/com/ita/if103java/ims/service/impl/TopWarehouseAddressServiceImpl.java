package com.ita.if103java.ims.service.impl;

import com.ita.if103java.ims.dao.TopWarehouseAddressDao;
import com.ita.if103java.ims.dto.warehouse.advice.TopWarehouseAddressDto;
import com.ita.if103java.ims.mapper.dto.TopWarehouseAddressDtoMapper;
import com.ita.if103java.ims.service.TopWarehouseAddressService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class TopWarehouseAddressServiceImpl implements TopWarehouseAddressService {

    private final TopWarehouseAddressDao dao;
    private final TopWarehouseAddressDtoMapper mapper;

    @Autowired
    public TopWarehouseAddressServiceImpl(TopWarehouseAddressDao dao, TopWarehouseAddressDtoMapper mapper) {
        this.dao = dao;
        this.mapper = mapper;
    }

    @Override
    public List<TopWarehouseAddressDto> findAll(Long accountId) {
        return mapper.toDtoList(dao.findAll(accountId));
    }
}
