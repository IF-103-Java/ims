package com.ita.if103java.ims.exception.service;

import com.ita.if103java.ims.exception.BaseRuntimeException;

public class SavedItemOutException extends BaseRuntimeException {
    public SavedItemOutException(String message) {
        super(message);
    }

    public SavedItemOutException() {
    }

    public SavedItemOutException(String message, Throwable cause) {
        super(message, cause);
    }

    public SavedItemOutException(Throwable cause) {
        super(cause);
    }
}
