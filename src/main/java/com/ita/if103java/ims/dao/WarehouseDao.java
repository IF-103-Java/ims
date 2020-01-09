package com.ita.if103java.ims.dao;

import com.ita.if103java.ims.entity.Warehouse;

import java.util.List;
import java.util.Map;

public interface WarehouseDao {
    Warehouse create(Warehouse warehouse);

    Warehouse update(Warehouse warehouse);

    boolean softDelete(Long id);

    List<Warehouse> findAll();

    Map<Long, String> findWarehouseNames(Long account_id);

    Warehouse findById(Long id);

    Integer findQuantityOfWarehousesByAccountId(Long accountId);

    List<Warehouse> findChildrenByTopWarehouseID(Long id);

    Integer findLevelByParentID(Long id);
}
