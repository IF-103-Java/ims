package com.ita.if103java.ims.dao;

import com.ita.if103java.ims.entity.TopWarehouseAddress;

import java.util.List;

public interface TopWarehouseAddressDao {
    List<TopWarehouseAddress> findAll(Long accountId);
}
