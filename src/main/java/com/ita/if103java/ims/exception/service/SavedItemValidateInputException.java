package com.ita.if103java.ims.exception.service;

import com.ita.if103java.ims.exception.BaseRuntimeException;

public class SavedItemValidateInputException extends BaseRuntimeException {
    public SavedItemValidateInputException(String message) {
        super(message);
    }

    public SavedItemValidateInputException() {
    }

    public SavedItemValidateInputException(String message, Throwable cause) {
        super(message, cause);
    }

    public SavedItemValidateInputException(Throwable cause) {
        super(cause);
    }
}
