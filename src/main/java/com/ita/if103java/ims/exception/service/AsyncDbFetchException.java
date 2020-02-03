package com.ita.if103java.ims.exception.service;

import com.ita.if103java.ims.exception.BaseRuntimeException;

public class AsyncDbFetchException extends BaseRuntimeException {
    public AsyncDbFetchException(String message) {
        super(message);
    }

    public AsyncDbFetchException() {
    }

    public AsyncDbFetchException(String message, Throwable cause) {
        super(message, cause);
    }

    public AsyncDbFetchException(Throwable cause) {
        super(cause);
    }
}
