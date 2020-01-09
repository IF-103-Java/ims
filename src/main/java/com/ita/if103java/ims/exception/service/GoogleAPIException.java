package com.ita.if103java.ims.exception.service;

import com.ita.if103java.ims.exception.BaseRuntimeException;

public class GoogleAPIException extends BaseRuntimeException {
    public GoogleAPIException(String message) {
        super(message);
    }

    public GoogleAPIException() {
    }

    public GoogleAPIException(String message, Throwable cause) {
        super(message, cause);
    }

    public GoogleAPIException(Throwable cause) {
        super(cause);
    }
}
