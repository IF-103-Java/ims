package com.ita.if103java.ims.exception;

import com.ita.if103java.ims.exception.BaseRuntimeException;

public class UserPermissionException extends BaseRuntimeException {

    public UserPermissionException(String message) {
        super(message);
    }

    public UserPermissionException() {
    }

    public UserPermissionException(String message, Throwable cause) {
        super(message, cause);
    }

    public UserPermissionException(Throwable cause) {
        super(cause);
    }

}
