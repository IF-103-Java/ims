package com.ita.if103java.ims.dto;

public class WarehouseAdviceDto {
    private WarehouseDto warehouse;
    private Double totalWeightedAvgDistance;

    public WarehouseAdviceDto() {
    }

    public WarehouseAdviceDto(WarehouseDto warehouse, Double totalWeightedAvgDistance) {
        this.warehouse = warehouse;
        this.totalWeightedAvgDistance = totalWeightedAvgDistance;
    }

    public WarehouseDto getWarehouse() {
        return warehouse;
    }

    public void setWarehouse(WarehouseDto warehouse) {
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
