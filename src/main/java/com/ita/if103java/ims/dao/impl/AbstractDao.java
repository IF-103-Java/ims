package com.ita.if103java.ims.dao.impl;

import com.ita.if103java.ims.exception.CRUDException;
import com.ita.if103java.ims.exception.EntityNotFoundException;
import org.slf4j.Logger;

public class AbstractDao {

    private static Logger logger;

    public static void setLogger(Logger logger) {
        AbstractDao.logger = logger;
    }

    protected EntityNotFoundException entityNotFoundException(EntityNotFoundException e, String message) {
        EntityNotFoundException exception = new EntityNotFoundException(e.toString());
        logger.error(message, e);
        return exception;
    }

    protected CRUDException crudException(RuntimeException e, String message) {
        CRUDException exception = new CRUDException(e.toString());
        logger.error(message, e);
        return exception;
    }

}
