package com.ita.if103java.ims.exception.service;

import com.ita.if103java.ims.exception.BaseRuntimeException;

public class MaxWarehousesLimitReachedException extends BaseRuntimeException {
    public MaxWarehousesLimitReachedException(String message) {
        super(message);
    }

    public MaxWarehousesLimitReachedException() {
    }

    public MaxWarehousesLimitReachedException(String message, Throwable cause) {
        super(message, cause);
    }

    public MaxWarehousesLimitReachedException(Throwable cause) {
        super(cause);
    }
}
