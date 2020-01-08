package com.ita.if103java.ims.exception.dao;

public class AddressNotFoundException extends EntityNotFoundException {

    public AddressNotFoundException(String message) {
        super(message);
    }

    public AddressNotFoundException() {
    }

    public AddressNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }

    public AddressNotFoundException(Throwable cause) {
        super(cause);
    }
}
