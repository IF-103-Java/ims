package com.ita.if103java.ims.exception.dao;

public class DashboardDataNotFoundException extends EntityNotFoundException {
    public DashboardDataNotFoundException(String message) {
        super(message);
    }

    public DashboardDataNotFoundException() {
    }

    public DashboardDataNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }

    public DashboardDataNotFoundException(Throwable cause) {
        super(cause);
    }
}
