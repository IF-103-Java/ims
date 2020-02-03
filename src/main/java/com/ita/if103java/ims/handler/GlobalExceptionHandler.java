package com.ita.if103java.ims.handler;

import com.ita.if103java.ims.dto.handler.ResponseMessageDto;
import com.ita.if103java.ims.exception.dao.CRUDException;
import com.ita.if103java.ims.exception.dao.EntityNotFoundException;
import com.ita.if103java.ims.exception.service.AssociateLimitReachedException;
import com.ita.if103java.ims.exception.service.GoogleAPIException;
import com.ita.if103java.ims.exception.service.ImpossibleWarehouseAdviceException;
import com.ita.if103java.ims.exception.service.MaxWarehouseDepthLimitReachedException;
import com.ita.if103java.ims.exception.service.MaxWarehousesLimitReachedException;
import com.ita.if103java.ims.exception.service.UpgradationException;
import com.ita.if103java.ims.exception.service.UserLimitReachedException;
import com.ita.if103java.ims.exception.service.UserOrPasswordIncorrectException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;


@RestControllerAdvice
public class GlobalExceptionHandler {
    private static final Logger LOGGER = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    @ExceptionHandler({EntityNotFoundException.class})
    public ResponseEntity<ResponseMessageDto> handleEntityNotFoundException(Exception e) {
        LOGGER.warn(e.getMessage(), e);
        return ResponseEntity
            .status(HttpStatus.NOT_FOUND)
            .body(new ResponseMessageDto(e.getMessage()));
    }

    @ExceptionHandler({UserOrPasswordIncorrectException.class})
    public ResponseEntity<ResponseMessageDto> handleUserOrPasswordIncorrectException(Exception e) {
        LOGGER.warn(e.getMessage(), e);
        return ResponseEntity
            .status(HttpStatus.UNAUTHORIZED)
            .body(new ResponseMessageDto(e.getMessage()));
    }

    @ExceptionHandler({CRUDException.class, GoogleAPIException.class})
    public ResponseEntity<ResponseMessageDto> handleServerErrorExceptions(Exception e) {
        LOGGER.error(e.getMessage(), e);
        return ResponseEntity
            .status(HttpStatus.INTERNAL_SERVER_ERROR)
            .body(new ResponseMessageDto(e.getMessage()));
    }

    @ExceptionHandler({ImpossibleWarehouseAdviceException.class,
        MaxWarehouseDepthLimitReachedException.class,
        MaxWarehousesLimitReachedException.class,
        AssociateLimitReachedException.class,
        UserLimitReachedException.class})
    public ResponseEntity<ResponseMessageDto> handleImpossibleWarehouseAdviceException(Exception e) {
        LOGGER.info(e.getMessage(), e);
        return ResponseEntity
            .status(HttpStatus.OK)
            .body(new ResponseMessageDto(e.getMessage()));
    }

    @ExceptionHandler({UpgradationException.class})
    public ResponseEntity<ResponseMessageDto> handleUpgradeException(Exception e) {
        LOGGER.error(e.getMessage(), e);
        return ResponseEntity
            .status(HttpStatus.BAD_REQUEST)
            .body(new ResponseMessageDto(e.getMessage()));
    }
}
