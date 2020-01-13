package com.ita.if103java.ims.exception.service;

import com.ita.if103java.ims.exception.BaseRuntimeException;

public class UserLimitReachedException extends BaseRuntimeException {
    public UserLimitReachedException(String message) {
        super(message);
    }

    public UserLimitReachedException() {
    }

    public UserLimitReachedException(String message, Throwable cause) {
        super(message, cause);
    }

    public UserLimitReachedException(Throwable cause) {
        super(cause);
    }
}
