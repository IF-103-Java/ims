package com.ita.if103java.ims.dto;

import com.google.maps.model.Distance;
import org.springframework.beans.factory.annotation.Autowired;

public class WarehouseToAssociateDistanceDto {
    private WarehouseAddressDto warehouseAddress;
    private AssociateAddressDto associateAddress;
    private Distance distance;

    public WarehouseToAssociateDistanceDto() {
    }


    @Autowired
    public WarehouseToAssociateDistanceDto(WarehouseAddressDto warehouseAddress,
                                           AssociateAddressDto associateAddress,
                                           Distance distance) {
        this.warehouseAddress = warehouseAddress;
        this.associateAddress = associateAddress;
        this.distance = distance;
    }

    public WarehouseAddressDto getWarehouseAddress() {
        return warehouseAddress;
    }

    public void setWarehouseAddress(WarehouseAddressDto warehouseAddress) {
        this.warehouseAddress = warehouseAddress;
    }

    public AssociateAddressDto getAssociateAddress() {
        return associateAddress;
    }

    public void setAssociateAddress(AssociateAddressDto associateAddress) {
        this.associateAddress = associateAddress;
    }

    public Distance getDistance() {
        return distance;
    }

    public void setDistance(Distance distance) {
        this.distance = distance;
    }

    @Override
    public String toString() {
        return "WarehouseToAssociateDistanceDto{" +
            "warehouseAddress=" + warehouseAddress +
            ", associateAddress=" + associateAddress +
            ", distance=" + distance.humanReadable +
            '}';
    }
}
