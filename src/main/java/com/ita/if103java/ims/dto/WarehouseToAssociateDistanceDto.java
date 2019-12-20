package com.ita.if103java.ims.dto;

import com.google.maps.model.Distance;

public class WarehouseToAssociateDistanceDto {
    private AddressDto warehouseAddress;
    private AddressDto associateAddress;
    private Distance distance;

    public WarehouseToAssociateDistanceDto() {
    }

    public WarehouseToAssociateDistanceDto(AddressDto warehouseAddress,
                                           AddressDto associateAddress,
                                           Distance distance) {
        this.warehouseAddress = warehouseAddress;
        this.associateAddress = associateAddress;
        this.distance = distance;
    }

    public AddressDto getWarehouseAddress() {
        return warehouseAddress;
    }

    public void setWarehouseAddress(AddressDto warehouseAddress) {
        this.warehouseAddress = warehouseAddress;
    }

    public AddressDto getAssociateAddress() {
        return associateAddress;
    }

    public void setAssociateAddress(AddressDto associateAddress) {
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
            "warehouseAddress=" + warehouseAddress.getCity() +
            ", associateAddress=" + associateAddress.getCity() +
            ", distance=" + distance.humanReadable +
            '}';
    }
}
