package com.ita.if103java.ims.exception;

public class BaseRuntimeException extends RuntimeException {
    private String name;
    public BaseRuntimeException(String message) {
        super(message);
    }
    public BaseRuntimeException(String message, String name) {
        super(message);
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
