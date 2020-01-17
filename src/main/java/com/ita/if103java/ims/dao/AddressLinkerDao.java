package com.ita.if103java.ims.dao;

import com.ita.if103java.ims.dto.AssociateAddressDto;
import com.ita.if103java.ims.dto.WarehouseAddressDto;

import java.util.List;

public interface AddressLinkerDao {
    List<AssociateAddressDto> findAssociateAddressesByIds(List<Long> ids);

    List<WarehouseAddressDto> findWarehouseAddressesByIds(List<Long> ids);
}
