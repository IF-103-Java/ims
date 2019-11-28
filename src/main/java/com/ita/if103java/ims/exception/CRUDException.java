package com.ita.if103java.ims.exception;

public class CRUDException extends BaseRuntimeException {
    public CRUDException(String message) {
        super(message);
    }

    public CRUDException(String message, String name) {
        super(message, name);
    }
}
