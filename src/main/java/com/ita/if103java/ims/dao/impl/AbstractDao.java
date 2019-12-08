package com.ita.if103java.ims.dao.impl;

import com.ita.if103java.ims.exception.CRUDException;
import com.ita.if103java.ims.exception.EntityNotFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class AbstractDao {
    private static final Logger LOG = LoggerFactory.getLogger(AbstractDao.class);

    protected EntityNotFoundException entityNotFoundException(EntityNotFoundException e, String message) {
        LOG.error(message, e);
        return e;
    }

    protected CRUDException crudException(RuntimeException e, String message) {
        CRUDException exception = new CRUDException(message, e);
        LOG.error(message, e);
        return exception;
    }
}