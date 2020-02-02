package com.ita.if103java.ims.dto.warehouse.advice;

import java.util.List;

public class WarehouseItemAdviceDto {
    private Long itemId;
    private List<WarehouseAdviceDto> warehouseAdvices;
    private BestAssociatesDto bestAssociates;

    public WarehouseItemAdviceDto() {
    }

    public WarehouseItemAdviceDto(Long itemId, List<WarehouseAdviceDto> warehouseAdvices, BestAssociatesDto bestAssociates) {
        this.itemId = itemId;
        this.warehouseAdvices = warehouseAdvices;
        this.bestAssociates = bestAssociates;
    }

    public Long getItemId() {
        return itemId;
    }

    public void setItemId(Long itemId) {
        this.itemId = itemId;
    }

    public List<WarehouseAdviceDto> getWarehouseAdvices() {
        return warehouseAdvices;
    }

    public void setWarehouseAdvices(List<WarehouseAdviceDto> warehouseAdvices) {
        this.warehouseAdvices = warehouseAdvices;
    }

    public BestAssociatesDto getBestAssociates() {
        return bestAssociates;
    }

    public void setBestAssociates(BestAssociatesDto bestAssociates) {
        this.bestAssociates = bestAssociates;
    }

    @Override
    public String toString() {
        return "WarehouseItemAdviceDto{" +
            "itemId=" + itemId +
            ", warehouseAdvices=" + warehouseAdvices +
            ", bestAssociates=" + bestAssociates +
            '}';
    }
}
