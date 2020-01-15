package com.ita.if103java.ims.exception.service;

import com.ita.if103java.ims.exception.BaseRuntimeException;

public class AssociateLimitReachedException extends BaseRuntimeException {
    public AssociateLimitReachedException(String message) {
        super(message);
    }

    public AssociateLimitReachedException() {
    }

    public AssociateLimitReachedException(String message, Throwable cause) {
        super(message, cause);
    }

    public AssociateLimitReachedException(Throwable cause) {
        super(cause);
    }
}
