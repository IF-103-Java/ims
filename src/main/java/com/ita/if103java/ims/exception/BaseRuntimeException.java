package com.ita.if103java.ims.exception;

public class BaseRuntimeException extends RuntimeException {

    BaseRuntimeException(String message) {
        super(message);
    }

    BaseRuntimeException() {
    }

    public BaseRuntimeException(String message, Throwable cause) {
        super(message, cause);
    }

    public BaseRuntimeException(Throwable cause) {
        super(cause);
    }
}
