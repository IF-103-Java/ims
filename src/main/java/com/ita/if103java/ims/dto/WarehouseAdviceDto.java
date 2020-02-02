package com.ita.if103java.ims.dto;

import com.ita.if103java.ims.dto.warehouse.advice.TopWarehouseAddressDto;

public class WarehouseAdviceDto {
    private TopWarehouseAddressDto warehouse;
    private Double totalWeightedAvgDistance;

    public WarehouseAdviceDto() {
    }

    public WarehouseAdviceDto(TopWarehouseAddressDto warehouse, Double totalWeightedAvgDistance) {
        this.warehouse = warehouse;
        this.totalWeightedAvgDistance = totalWeightedAvgDistance;
    }

    public TopWarehouseAddressDto getWarehouse() {
        return warehouse;
    }

    public void setWarehouse(TopWarehouseAddressDto warehouse) {
        this.warehouse = warehouse;
    }

    public Double getTotalWeightedAvgDistance() {
        return totalWeightedAvgDistance;
    }

    public void setTotalWeightedAvgDistance(Double totalWeightedAvgDistance) {
        this.totalWeightedAvgDistance = totalWeightedAvgDistance;
    }

    @Override
    public String toString() {
        return "WarehouseAdviceDto{" +
            "warehouse=" + warehouse +
            ", totalWeightedAvgDistance=" + totalWeightedAvgDistance +
            '}';
    }
}
