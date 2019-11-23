package com.ita.if103java.ims.exception;

public class CRUDException extends RuntimeException {

    private String name;

    public CRUDException(String message) {
        super(message);
    }

    public CRUDException(String message, String name) {
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
