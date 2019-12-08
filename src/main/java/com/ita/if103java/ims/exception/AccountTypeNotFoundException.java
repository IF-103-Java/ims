package com.ita.if103java.ims.exception;

public class AccountTypeNotFoundException extends EntityNotFoundException {
    public AccountTypeNotFoundException(String message) {
        super(message);
    }

    public AccountTypeNotFoundException() {
    }

    public AccountTypeNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }

    public AccountTypeNotFoundException(Throwable cause) {
        super(cause);
    }
}
