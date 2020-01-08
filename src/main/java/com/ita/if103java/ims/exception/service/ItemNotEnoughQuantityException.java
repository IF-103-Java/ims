package com.ita.if103java.ims.exception.service;

import com.ita.if103java.ims.exception.BaseRuntimeException;

public class ItemNotEnoughQuantityException extends BaseRuntimeException {
    public ItemNotEnoughQuantityException(String message) {
        super(message);
    }

    public ItemNotEnoughQuantityException() {
    }

    public ItemNotEnoughQuantityException(String message, Throwable cause) {
        super(message, cause);
    }

    public ItemNotEnoughQuantityException(Throwable cause) {
        super(cause);
    }
}
