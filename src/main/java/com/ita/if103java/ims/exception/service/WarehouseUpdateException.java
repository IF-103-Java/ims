package com.ita.if103java.ims.exception.service;

import com.ita.if103java.ims.exception.BaseRuntimeException;

public class WarehouseUpdateException extends BaseRuntimeException {
    public WarehouseUpdateException(String message) {
        super(message);
    }

    public WarehouseUpdateException() {
    }

    public WarehouseUpdateException(String message, Throwable cause) {
        super(message, cause);
    }

    public WarehouseUpdateException(Throwable cause) {
        super(cause);
    }
}
