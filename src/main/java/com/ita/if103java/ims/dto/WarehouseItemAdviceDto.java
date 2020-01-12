package com.ita.if103java.ims.dto;

import java.util.List;

public class WarehouseItemAdviceDto {
    private ItemDto item;
    private List<WarehouseAdviceDto> warehouseAdvices;
    private List<AssociateDto> clients;
    private List<AssociateDto> suppliers;

    public WarehouseItemAdviceDto() {
    }

    public WarehouseItemAdviceDto(ItemDto item, List<WarehouseAdviceDto> warehouseAdvices,
                                  List<AssociateDto> clients, List<AssociateDto> suppliers) {
        this.item = item;
        this.warehouseAdvices = warehouseAdvices;
        this.clients = clients;
        this.suppliers = suppliers;
    }

    public ItemDto getItem() {
        return item;
    }

    public void setItem(ItemDto item) {
        this.item = item;
    }

    public List<WarehouseAdviceDto> getWarehouseAdvices() {
        return warehouseAdvices;
    }

    public void setWarehouseAdvices(List<WarehouseAdviceDto> warehouseAdvices) {
        this.warehouseAdvices = warehouseAdvices;
    }

    public List<AssociateDto> getClients() {
        return clients;
    }

    public void setClients(List<AssociateDto> clients) {
        this.clients = clients;
    }

    public List<AssociateDto> getSuppliers() {
        return suppliers;
    }

    public void setSuppliers(List<AssociateDto> suppliers) {
        this.suppliers = suppliers;
    }

    @Override
    public String toString() {
        return "WarehouseItemAdviceDto{" +
            "item=" + item +
            ", warehouseAdvices=" + warehouseAdvices +
            ", clients=" + clients +
            ", suppliers=" + suppliers +
            '}';
    }
}
