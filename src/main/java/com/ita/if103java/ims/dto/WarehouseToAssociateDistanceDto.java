package com.ita.if103java.ims.dto;

import com.google.maps.model.Distance;
import com.google.maps.model.DistanceMatrixElementStatus;

public class WarehouseToAssociateDistanceDto {
    private WarehouseAddressDto warehouseAddress;
    private AssociateAddressDto associateAddress;
    private Distance distance;
    private DistanceMatrixElementStatus status;

    public WarehouseToAssociateDistanceDto() {
    }

    public WarehouseToAssociateDistanceDto(WarehouseAddressDto warehouseAddress,
                                           AssociateAddressDto associateAddress,
                                           Distance distance,
                                           DistanceMatrixElementStatus status) {
        this.warehouseAddress = warehouseAddress;
        this.associateAddress = associateAddress;
        this.distance = distance;
        this.status = status;
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

    public DistanceMatrixElementStatus getStatus() {
        return status;
    }

    public void setStatus(DistanceMatrixElementStatus status) {
        this.status = status;
    }

    @Override
    public String toString() {
        return "WarehouseToAssociateDistanceDto{" +
            "warehouseAddress=" + warehouseAddress +
            ", associateAddress=" + associateAddress +
            ", distance=" + distance +
            ", status=" + status +
            '}';
    }
}
