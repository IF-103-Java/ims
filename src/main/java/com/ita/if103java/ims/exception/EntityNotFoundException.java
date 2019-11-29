package com.ita.if103java.ims.exception;

public class EntityNotFoundException extends BaseRuntimeException {

    public EntityNotFoundException(String message) {
        super(message);
    }

    public EntityNotFoundException() {
        super();
    }

    public EntityNotFoundException(String message, String name) {
        super(message, name);
    }

}
