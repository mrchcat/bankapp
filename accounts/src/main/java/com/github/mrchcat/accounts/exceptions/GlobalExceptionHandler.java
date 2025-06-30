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

    @ExceptionHandler(UserNotUniqueProperties.class)
    public ErrorResponse handleUserNotUniqueProperties(UserNotUniqueProperties ex) {
        return ErrorResponse.builder(ex, HttpStatus.BAD_REQUEST, "свойства не уникальны")
                .header("X-not-unique", String.join(";", ex.duplicateProperties))
                .build();
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ErrorResponse handleIllegalArgument(IllegalArgumentException ex) {
        String message = String.format("Некорректный запрос", ex.getMessage());
        return ErrorResponse.create(ex, HttpStatus.BAD_REQUEST, message);
    }


}
