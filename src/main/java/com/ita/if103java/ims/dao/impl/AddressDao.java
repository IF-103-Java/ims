package com.ita.if103java.ims.dao.impl;

import com.ita.if103java.ims.entity.Address;

public interface AddressDao {
    Address createWarehouseAddress(Long warehouseId, Address address);

    Address createAssociateAddress(Long associateId, Address address);

    Address updateWarehouseAddress(Long warehouseId, Address address);

    Address updateAssociateAddress(Long associateId, Address address);

    Address findById(Long addressId);

    Address findByWarehouseId(Long warehouseId);

    Address findByAssociateId(Long associateId);
}
