package com.ita.if103java.ims.dto.warehouse.advice;

import com.google.maps.model.Distance;
import com.google.maps.model.DistanceMatrixElementStatus;
import com.ita.if103java.ims.dto.warehouse.advice.BestWeightedAssociatesDto.WeightedBestAssociateDto;

import java.util.Objects;

public class WarehouseToAssociateDistanceDto {
    private TopWarehouseAddressDto warehouse;
    private WeightedBestAssociateDto associate;
    private Distance distance;
    private DistanceMatrixElementStatus status;

    public WarehouseToAssociateDistanceDto() {
    }

    public WarehouseToAssociateDistanceDto(TopWarehouseAddressDto warehouse, WeightedBestAssociateDto associate,
                                           Distance distance, DistanceMatrixElementStatus status) {
        this.warehouse = warehouse;
        this.associate = associate;
        this.distance = distance;
        this.status = status;
    }

    public TopWarehouseAddressDto getWarehouse() {
        return warehouse;
    }

    public void setWarehouse(TopWarehouseAddressDto warehouse) {
        this.warehouse = warehouse;
    }

    public WeightedBestAssociateDto getAssociate() {
        return associate;
    }

    public void setAssociate(WeightedBestAssociateDto associate) {
        this.associate = associate;
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
            "warehouse=" + warehouse +
            ", associate=" + associate +
            ", distance=" + distance +
            ", status=" + status +
            '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        WarehouseToAssociateDistanceDto that = (WarehouseToAssociateDistanceDto) o;
        return warehouse.equals(that.warehouse) &&
            associate.equals(that.associate) &&
            distance.equals(that.distance) &&
            status == that.status;
    }

    @Override
    public int hashCode() {
        return Objects.hash(warehouse, associate, distance, status);
    }
}
