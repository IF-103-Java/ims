package com.ita.if103java.ims.exception.service;

import com.ita.if103java.ims.exception.BaseRuntimeException;

public class SavedItemAddException extends BaseRuntimeException {
    public SavedItemAddException(String message) {
        super(message);
    }

    public SavedItemAddException() {
    }

    public SavedItemAddException(String message, Throwable cause) {
        super(message, cause);
    }

    public SavedItemAddException(Throwable cause) {
        super(cause);
    }
}
