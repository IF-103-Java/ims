package com.ita.if103java.ims.exception.service;

import com.ita.if103java.ims.exception.BaseRuntimeException;

public class ImpossibleWarehouseAdviceException extends BaseRuntimeException {
    public ImpossibleWarehouseAdviceException(String message) {
        super(message);
    }

    public ImpossibleWarehouseAdviceException() {
    }

    public ImpossibleWarehouseAdviceException(String message, Throwable cause) {
        super(message, cause);
    }

    public ImpossibleWarehouseAdviceException(Throwable cause) {
        super(cause);
    }
}
