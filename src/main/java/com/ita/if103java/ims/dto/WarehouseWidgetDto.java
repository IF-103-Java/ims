package com.ita.if103java.ims.dto;

import org.springframework.stereotype.Component;

import javax.validation.constraints.NotNull;

@Component
public class WarehouseWidgetDto {
    @NotNull
    private Long warahouseCapacity;
    @NotNull
    private Long warehouseLoad;

    public Long getWarahouseCapacity() {
        return warahouseCapacity;
    }

    public void setWarahouseCapacity(Long warahouseCapacity) {
        this.warahouseCapacity = warahouseCapacity;
    }

    public Long getWarehouseLoad() {
        return warehouseLoad;
    }

    public void setWarehouseLoad(Long warehouseLoad) {
        this.warehouseLoad = warehouseLoad;
    }

    @Override
    public String toString() {
        return "WarehouseWidgetDto{" +
            "warahouseCapacity=" + warahouseCapacity +
            ", warehouseLoad=" + warehouseLoad +
            '}';
    }

    public WarehouseWidgetDto() {
    }

}
