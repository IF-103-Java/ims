package com.ita.if103java.ims.exception.dao;

public class AssociateEntityNotFoundException extends EntityNotFoundException {
    public AssociateEntityNotFoundException(String message) {
        super(message);
    }

    public AssociateEntityNotFoundException() {
    }

    public AssociateEntityNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }

    public AssociateEntityNotFoundException(Throwable cause) {
        super(cause);
    }
}
