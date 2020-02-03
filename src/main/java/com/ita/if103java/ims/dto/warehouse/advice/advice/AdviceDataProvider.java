package com.ita.if103java.ims.dto.warehouse.advice.advice;

import com.ita.if103java.ims.dto.warehouse.advice.TopWarehouseAddressDto;
import com.ita.if103java.ims.dto.warehouse.advice.associate.BestWeightedAssociatesDto;

import java.util.List;

public class AdviceDataProvider {
    private List<TopWarehouseAddressDto> warehouseAddresses;
    private BestWeightedAssociatesDto bestAssociates;

    public AdviceDataProvider() {
    }

    public AdviceDataProvider(List<TopWarehouseAddressDto> warehouseAddresses, BestWeightedAssociatesDto bestAssociates) {
        this.warehouseAddresses = warehouseAddresses;
        this.bestAssociates = bestAssociates;
    }

    public List<TopWarehouseAddressDto> getWarehouseAddresses() {
        return warehouseAddresses;
    }

    public void setWarehouseAddresses(List<TopWarehouseAddressDto> warehouseAddresses) {
        this.warehouseAddresses = warehouseAddresses;
    }

    public BestWeightedAssociatesDto getBestAssociates() {
        return bestAssociates;
    }

    public void setBestAssociates(BestWeightedAssociatesDto bestAssociates) {
        this.bestAssociates = bestAssociates;
    }

    @Override
    public String toString() {
        return "AdviceDataProvider{" +
            "warehouseAddresses=" + warehouseAddresses +
            ", bestAssociates=" + bestAssociates +
            '}';
    }
}
