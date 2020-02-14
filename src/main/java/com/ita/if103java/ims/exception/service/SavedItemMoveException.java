package com.ita.if103java.ims.exception.service;

import com.ita.if103java.ims.exception.BaseRuntimeException;

public class SavedItemMoveException extends BaseRuntimeException {
    public SavedItemMoveException(String message) {
        super(message);
    }

    public SavedItemMoveException() {
    }

    public SavedItemMoveException(String message, Throwable cause) {
        super(message, cause);
    }

    public SavedItemMoveException(Throwable cause) {
        super(cause);
    }
}
