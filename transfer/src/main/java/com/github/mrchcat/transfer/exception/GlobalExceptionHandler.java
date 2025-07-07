package com.github.mrchcat.transfer.exception;


import org.springframework.http.HttpStatus;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.ErrorResponse;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(IllegalArgumentException.class)
    public ErrorResponse handleIllegalArgument(IllegalArgumentException ex) {
        return ErrorResponse.create(ex, HttpStatus.BAD_REQUEST, "Ошибка: Некорректный запрос " + ex.getMessage());
    }

    @ExceptionHandler(UsernameNotFoundException.class)
    public ErrorResponse handleIllegalArgument(UsernameNotFoundException ex) {
        String message = String.format("Клиент c username=%s не найден", ex.getMessage());
        return ErrorResponse.create(ex, HttpStatus.NOT_FOUND, message);
    }

    @ExceptionHandler(BlockerException.class)
    public ErrorResponse handleIllegalArgument(BlockerException ex) {
        return ErrorResponse.create(ex, HttpStatus.FORBIDDEN, "Операция запрещена по причине: " + ex.getMessage());
    }

    @ExceptionHandler(NotEnoughMoney.class)
    public ErrorResponse handleIllegalArgument(NotEnoughMoney ex) {
        return ErrorResponse.create(ex, HttpStatus.FORBIDDEN, "Не хватает средств для проведения операции");
    }

}
