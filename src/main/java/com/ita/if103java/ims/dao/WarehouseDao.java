package com.ita.if103java.ims.dao;

import com.ita.if103java.ims.entity.Warehouse;
import java.util.List;

public interface WarehouseDao {
    Warehouse create (Warehouse warehouse);

    Warehouse update (Warehouse warehouse);

    boolean deleteWarehouse(long id);

    List<Warehouse> findAll();

    Warehouse findById(long id);

    List<Warehouse> findByParent(long id);
}
