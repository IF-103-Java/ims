package com.ita.if103java.ims.exception.service;

import com.ita.if103java.ims.exception.BaseRuntimeException;

public class UpgradationException extends BaseRuntimeException {
    public UpgradationException(String message) {
        super(message);
    }

    public UpgradationException() {
    }

    public UpgradationException(String message, Throwable cause) {
        super(message, cause);
    }

    public UpgradationException(Throwable cause) {
        super(cause);
    }
}
