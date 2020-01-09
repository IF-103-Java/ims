package com.ita.if103java.ims.exception.dao;

public class TransactionNotFoundException extends EntityNotFoundException {

    public TransactionNotFoundException(String message) {
        super(message);
    }

    public TransactionNotFoundException() {
    }

    public TransactionNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }

    public TransactionNotFoundException(Throwable cause) {
        super(cause);
    }
}
