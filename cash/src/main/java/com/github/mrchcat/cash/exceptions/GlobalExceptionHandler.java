package com.github.mrchcat.cash.exceptions;


import org.springframework.http.HttpStatus;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.ErrorResponse;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(IllegalArgumentException.class)
    public ErrorResponse handleIllegalArgument(IllegalArgumentException ex) {
        String message = String.format("Некорректный запрос", ex.getMessage());
        return ErrorResponse.create(ex, HttpStatus.BAD_REQUEST, message);
    }

    @ExceptionHandler(UsernameNotFoundException.class)
    public ErrorResponse handleIllegalArgument(UsernameNotFoundException ex) {
        String message = String.format("Клиент c username=%s не найден", ex.getMessage());
        return ErrorResponse.create(ex, HttpStatus.NOT_FOUND, message);
    }
}
