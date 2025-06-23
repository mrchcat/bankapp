package com.github.mrchcat.accounts.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.ErrorResponse;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(UsernameNotFoundException.class)
    public ErrorResponse handleIllegalArgument(UsernameNotFoundException ex) {
        String message = String.format("Клиент c username=%s не найден", ex.getMessage());
        return ErrorResponse.create(ex, HttpStatus.NOT_FOUND, message);
    }

}
