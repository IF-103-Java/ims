package com.ita.if103java.ims.dao;

import com.ita.if103java.ims.entity.Warehouse;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface WarehouseDao {
    Warehouse create(Warehouse warehouse);

    Warehouse update(Warehouse warehouse);

    boolean softDelete(Long id);

    List<Warehouse> findAll(Pageable pageable, Long accountId);

    Warehouse findById(Long id);

    Integer findQuantityOfWarehousesByAccountId(Long accountId);

    List<Warehouse> findByTopWarehouseID(Long id);

    Integer findLevelByParentID(Long id);
}
