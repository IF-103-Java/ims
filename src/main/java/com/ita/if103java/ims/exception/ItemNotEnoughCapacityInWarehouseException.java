package com.ita.if103java.ims.exception;

public class ItemNotEnoughCapacityInWarehouseException  extends BaseRuntimeException {
    public ItemNotEnoughCapacityInWarehouseException(String message) {
        super(message);
    }

    public ItemNotEnoughCapacityInWarehouseException() {
    }

    public ItemNotEnoughCapacityInWarehouseException(String message, Throwable cause) {
        super(message, cause);
    }

    public ItemNotEnoughCapacityInWarehouseException(Throwable cause) {
        super(cause);
    }
}
