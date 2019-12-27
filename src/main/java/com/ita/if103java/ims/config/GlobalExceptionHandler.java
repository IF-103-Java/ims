package com.ita.if103java.ims.config;

import com.ita.if103java.ims.dto.PesponseWrapperDto;
import com.ita.if103java.ims.exception.CRUDException;
import com.ita.if103java.ims.exception.EntityNotFoundException;
import com.ita.if103java.ims.exception.GoogleAPIException;
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
    public PesponseWrapperDto handleEntityNotFoundException(Exception e) {
        LOGGER.warn(e.getMessage(), e);
        return new PesponseWrapperDto(HttpStatus.NOT_FOUND.value(), e.getMessage());
    }

    @ExceptionHandler({CRUDException.class, GoogleAPIException.class})
    public PesponseWrapperDto handleCRUDException(Exception e) {
        LOGGER.error(e.getMessage(), e);
        return new PesponseWrapperDto(HttpStatus.INTERNAL_SERVER_ERROR.value(), e.getMessage());
    }

    @ExceptionHandler({UserOrPasswordIncorrectException.class})
    public PesponseWrapperDto handleUserOrPasswordIncorrectException(Exception e) {
        LOGGER.warn(e.getMessage(), e);
        return new PesponseWrapperDto(HttpStatus.UNAUTHORIZED.value(), e.getMessage());
    }

}
