package com.ita.if103java.ims.exception.service;

import com.ita.if103java.ims.exception.BaseRuntimeException;

public class WarehouseDeleteException extends BaseRuntimeException {
    public WarehouseDeleteException(String message) {
        super(message);
    }

    public WarehouseDeleteException() {
    }

    public WarehouseDeleteException(String message, Throwable cause) {
        super(message, cause);
    }

    public WarehouseDeleteException(Throwable cause) {
        super(cause);
    }
}
