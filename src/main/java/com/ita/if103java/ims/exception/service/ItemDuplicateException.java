package com.ita.if103java.ims.exception.service;

import com.ita.if103java.ims.exception.BaseRuntimeException;

public class ItemDuplicateException extends BaseRuntimeException {
    public ItemDuplicateException(String message) {
        super(message);
    }

    public ItemDuplicateException() {
    }

    public ItemDuplicateException(String message, Throwable cause) {
        super(message, cause);
    }

    public ItemDuplicateException(Throwable cause) {
        super(cause);
    }
}
