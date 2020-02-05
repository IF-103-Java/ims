package com.ita.if103java.ims.exception.service;

import com.ita.if103java.ims.exception.BaseRuntimeException;

public class WarehouseCreateException extends BaseRuntimeException {
    public WarehouseCreateException(String message) {
        super(message);
    }

    public WarehouseCreateException() {
    }

    public WarehouseCreateException(String message, Throwable cause) {
        super(message, cause);
    }

    public WarehouseCreateException(Throwable cause) {
        super(cause);
    }
}
