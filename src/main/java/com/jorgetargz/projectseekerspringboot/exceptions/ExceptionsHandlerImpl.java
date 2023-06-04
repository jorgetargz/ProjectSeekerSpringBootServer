package com.jorgetargz.projectseekerspringboot.exceptions;

import com.jorgetargz.projectseekerspringboot.dto.error.ErrorDTO;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import java.nio.file.AccessDeniedException;
import java.sql.Timestamp;

@ControllerAdvice
@Component
public class ExceptionsHandlerImpl extends ResponseEntityExceptionHandler {

    private final HttpServletRequest httpServletRequest;

    @Autowired
    public ExceptionsHandlerImpl(HttpServletRequest httpServletRequest) {
        this.httpServletRequest = httpServletRequest;
    }

    @ResponseStatus(HttpStatus.NOT_FOUND)
    @ExceptionHandler({
            NotFoundException.class,
            NullPointerException.class}
    )
    public ResponseEntity<ErrorDTO> handleNotFoundException(RuntimeException e) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(new ErrorDTO(
                        new Timestamp(System.currentTimeMillis()),
                        HttpStatus.NOT_FOUND.value(),
                        (String) httpServletRequest.getAttribute("firebase-error"),
                        e.getMessage(),
                        "Not found Error check the url and the parameters especially the ids"
                ));
    }

    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    @ExceptionHandler({
            AccessDeniedException.class,
            UnauthorizedException.class}
    )
    public ResponseEntity<ErrorDTO> handleAccessException(RuntimeException e) {
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .body(new ErrorDTO(
                        new Timestamp(System.currentTimeMillis()),
                        HttpStatus.UNAUTHORIZED.value(),
                        (String) httpServletRequest.getAttribute("firebase-error"),
                        e.getMessage(),
                        "Unauthorized Error check you have a valid session and the permissions to access this resource"
                ));
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler({
            DataIntegrityViolationException.class,
            IllegalArgumentException.class,
            BadRequestException.class})
    public ResponseEntity<ErrorDTO> handleDataIntegrityException(DataIntegrityViolationException e) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(new ErrorDTO(
                        new Timestamp(System.currentTimeMillis()),
                        HttpStatus.BAD_REQUEST.value(),
                        (String) httpServletRequest.getAttribute("firebase-error"),
                        e.getMessage(),
                        "Bad Request Error check the parameters and the body of the request"
                ));
    }

}
