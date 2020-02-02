package com.ita.if103java.ims.dto.warehouse.advice;

import java.util.List;

public class WarehouseToAssociateDistancesDto {
    private List<WarehouseToAssociateDistanceDto> supplierWarehouseDistances;
    private List<WarehouseToAssociateDistanceDto> clientWarehouseDistances;

    public WarehouseToAssociateDistancesDto(List<WarehouseToAssociateDistanceDto> supplierWarehouseDistances,
                                            List<WarehouseToAssociateDistanceDto> clientWarehouseDistances) {
        this.supplierWarehouseDistances = supplierWarehouseDistances;
        this.clientWarehouseDistances = clientWarehouseDistances;
    }

    public List<WarehouseToAssociateDistanceDto> getClientWarehouseDistances() {
        return clientWarehouseDistances;
    }

    public void setClientWarehouseDistances(List<WarehouseToAssociateDistanceDto> clientWarehouseDistances) {
        this.clientWarehouseDistances = clientWarehouseDistances;
    }

    public List<WarehouseToAssociateDistanceDto> getSupplierWarehouseDistances() {
        return supplierWarehouseDistances;
    }

    public void setSupplierWarehouseDistances(List<WarehouseToAssociateDistanceDto> supplierWarehouseDistances) {
        this.supplierWarehouseDistances = supplierWarehouseDistances;
    }

    @Override
    public String toString() {
        return "WarehouseToAssociateDistancesDto{" +
            "clientWarehouseDistances=" + clientWarehouseDistances +
            ", supplierWarehouseDistances=" + supplierWarehouseDistances +
            '}';
    }
}
