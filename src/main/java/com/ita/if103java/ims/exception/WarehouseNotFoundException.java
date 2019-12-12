package com.ita.if103java.ims.exception;

public class WarehouseNotFoundException extends EntityNotFoundException {
    public WarehouseNotFoundException(String message) {
        super(message);
    }

    public WarehouseNotFoundException() {

    }

    public WarehouseNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }
}
