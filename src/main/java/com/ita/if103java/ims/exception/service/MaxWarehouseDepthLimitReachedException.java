package com.ita.if103java.ims.exception.service;

import com.ita.if103java.ims.exception.BaseRuntimeException;

public class MaxWarehouseDepthLimitReachedException extends BaseRuntimeException {
    public MaxWarehouseDepthLimitReachedException(String message) {
        super(message);
    }

    public MaxWarehouseDepthLimitReachedException() {
    }

    public MaxWarehouseDepthLimitReachedException(String message, Throwable cause) {
        super(message, cause);
    }

    public MaxWarehouseDepthLimitReachedException(Throwable cause) {
        super(cause);
    }
}
