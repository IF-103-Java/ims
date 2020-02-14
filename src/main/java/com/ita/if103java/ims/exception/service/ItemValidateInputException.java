package com.ita.if103java.ims.exception.service;

import com.ita.if103java.ims.exception.BaseRuntimeException;

public class ItemValidateInputException extends BaseRuntimeException {
    public ItemValidateInputException(String message) {
        super(message);
    }

    public ItemValidateInputException() {
    }

    public ItemValidateInputException(String message, Throwable cause) {
        super(message, cause);
    }

    public ItemValidateInputException(Throwable cause) {
        super(cause);
    }
}
