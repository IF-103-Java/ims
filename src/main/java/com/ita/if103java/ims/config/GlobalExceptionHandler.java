package com.ita.if103java.ims.config;

import com.ita.if103java.ims.exception.CRUDException;
import com.ita.if103java.ims.exception.EntityNotFoundException;
import com.ita.if103java.ims.exception.UserOrPasswordIncorrectException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import javax.servlet.http.HttpServletRequest;

@RestControllerAdvice
public class GlobalExceptionHandler {
    private static final Logger LOGGER = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    @ExceptionHandler({EntityNotFoundException.class, UserOrPasswordIncorrectException.class})
    public ResponseEntity handleEntityNotFoundException(HttpServletRequest req, Exception e) {
        LOGGER.warn(e.getMessage(), e);
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
            .body(new String[]{e.getMessage()});
    }

    @ExceptionHandler({CRUDException.class})
    public ResponseEntity handleCRUDException(HttpServletRequest req, Exception e) {
        LOGGER.error(e.getMessage(), e);
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
            .body(new String[]{e.getMessage()});
    }
}
