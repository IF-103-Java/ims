package com.ita.if103java.ims.exception.security;

public class InvalidJwtTokenException extends RuntimeException {

    public InvalidJwtTokenException() {
    }

    public InvalidJwtTokenException(String message) {
        super(message);
    }

    public InvalidJwtTokenException(String message, Throwable cause) {
        super(message, cause);
    }

    public InvalidJwtTokenException(Throwable cause) {
        super(cause);
    }

}
