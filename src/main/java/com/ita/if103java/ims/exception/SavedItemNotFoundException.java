package com.ita.if103java.ims.exception;

public class SavedItemNotFoundException extends EntityNotFoundException {
    public SavedItemNotFoundException(String message) {
        super(message);
    }

    public SavedItemNotFoundException() {
    }

    public SavedItemNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }

    public SavedItemNotFoundException(Throwable cause) {
        super(cause);
    }
}
