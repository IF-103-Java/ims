package com.ita.if103java.ims.exception;

public class EntityNotFoundException extends RuntimeException {

    private String name;

    public EntityNotFoundException(String message) {
        super(message);
    }

    public EntityNotFoundException(String message, String name) {
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
