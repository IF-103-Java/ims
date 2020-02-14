package com.ita.if103java.ims.exception.service;

import com.ita.if103java.ims.exception.BaseRuntimeException;

public class BottomLevelWarehouseException extends BaseRuntimeException {
    public BottomLevelWarehouseException(String message) {
        super(message);
    }

    public BottomLevelWarehouseException() {
    }

    public BottomLevelWarehouseException(String message, Throwable cause) {
        super(message, cause);
    }

    public BottomLevelWarehouseException(Throwable cause) {
        super(cause);
    }
}
