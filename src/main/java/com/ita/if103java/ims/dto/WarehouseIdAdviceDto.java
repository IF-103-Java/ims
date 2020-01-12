package com.ita.if103java.ims.dto;

public class WarehouseIdAdviceDto {
    private Long warehouseId;
    private Double weightedAvgDistance;

    public WarehouseIdAdviceDto() {
    }

    public WarehouseIdAdviceDto(Long warehouseId, Double weightedAvgDistance) {
        this.warehouseId = warehouseId;
        this.weightedAvgDistance = weightedAvgDistance;
    }

    public Long getWarehouseId() {
        return warehouseId;
    }

    public void setWarehouseId(Long warehouseId) {
        this.warehouseId = warehouseId;
    }

    public Double getWeightedAvgDistance() {
        return weightedAvgDistance;
    }

    public void setWeightedAvgDistance(Double weightedAvgDistance) {
        this.weightedAvgDistance = weightedAvgDistance;
    }

    @Override
    public String toString() {
        return "WarehouseIdAdviceDto{" +
            "warehouseId=" + warehouseId +
            ", weightedAvgDistance=" + weightedAvgDistance +
            '}';
    }
}
