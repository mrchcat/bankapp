package com.github.mrchcat.accounts.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.ErrorResponse;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.util.NoSuchElementException;

@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(UsernameNotFoundException.class)
    public ErrorResponse handleIllegalArgument(UsernameNotFoundException ex) {
        String message = String.format(ex.getMessage());
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
        return ErrorResponse.create(ex, HttpStatus.BAD_REQUEST, "Некорректный запрос " + ex.getMessage());
    }

    @ExceptionHandler(NotEnoughMoney.class)
    public ErrorResponse handleIllegalArgument(NotEnoughMoney ex) {
        return ErrorResponse.create(ex, HttpStatus.BAD_REQUEST, "Недостаточно средств");
    }

    @ExceptionHandler(TransactionWasCompletedAlready.class)
    public ErrorResponse handleUserNotUniqueProperties(TransactionWasCompletedAlready ex) {
        return ErrorResponse.builder(ex, HttpStatus.BAD_REQUEST, "транзакция уже есть в базе")
                .header("X-not-unique", String.join(";", ex.getMessage()))
                .build();
    }

    @ExceptionHandler(NoSuchElementException.class)
    public ErrorResponse handleUserNotUniqueProperties(NoSuchElementException ex) {
        return ErrorResponse.builder(ex, HttpStatus.BAD_REQUEST, "требуемый объект отсутствует")
                .header("X-not-exist", String.join(";", ex.getMessage()))
                .build();
    }

}
