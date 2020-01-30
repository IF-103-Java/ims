package com.ita.if103java.ims.dao;

import com.ita.if103java.ims.entity.Warehouse;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Map;

public interface WarehouseDao {
    Warehouse create(Warehouse warehouse);

    Warehouse update(Warehouse warehouse);

    boolean softDelete(Long id);

    List<Warehouse> findAllTopLevel(Pageable pageable, Long accountId);

    Map<Long, String> findAllWarehouseNames(Long account_id);

    Map<Long, String> findWarehouseNamesById(List<Long> idList);

    Warehouse findById(Long id, Long accountId);

    Integer findQuantityOfWarehousesByAccountId(Long accountId);

    List<Warehouse> findByTopWarehouseID(Long id, Long accountId);

    Integer findLevelByParentID(Long id);
}
