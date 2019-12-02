package com.ita.if103java.ims.exception;

public class BaseRuntimeException extends RuntimeException {

    BaseRuntimeException(String message){
        super(message);
    }

    BaseRuntimeException() {
    }
}
