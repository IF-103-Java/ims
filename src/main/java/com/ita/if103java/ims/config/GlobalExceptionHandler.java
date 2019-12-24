package com.ita.if103java.ims.config;

import com.ita.if103java.ims.dto.ErrorInfoDto;
import com.ita.if103java.ims.exception.CRUDException;
import com.ita.if103java.ims.exception.EntityNotFoundException;
import com.ita.if103java.ims.exception.UserOrPasswordIncorrectException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {
    private static final Logger LOGGER = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    @ExceptionHandler({EntityNotFoundException.class})
    public ErrorInfoDto handleEntityNotFoundException(Exception e) {
        LOGGER.warn(e.getMessage(), e);
        return new ErrorInfoDto(HttpStatus.NOT_FOUND.value(), e.getMessage());
    }

    @ExceptionHandler({CRUDException.class})
    public ErrorInfoDto handleCRUDException(Exception e) {
        LOGGER.error(e.getMessage(), e);
        return new ErrorInfoDto(HttpStatus.INTERNAL_SERVER_ERROR.value(), e.getMessage());
    }

    @ExceptionHandler({UserOrPasswordIncorrectException.class})
    public ErrorInfoDto handleUserOrPasswordIncorrectException(Exception e) {
        LOGGER.warn(e.getMessage(), e);
        return new ErrorInfoDto(HttpStatus.UNAUTHORIZED.value(), e.getMessage());
    }
}
