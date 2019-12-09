package com.ita.if103java.ims.exception;

public class ItemNotFoundException extends EntityNotFoundException {
    public ItemNotFoundException(String message) {
        super(message);
    }

    public ItemNotFoundException() {
    }

    public ItemNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }

    public ItemNotFoundException(Throwable cause) {
        super(cause);
    }
}
